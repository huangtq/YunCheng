[CmdletBinding()]
param(
    [ValidateSet("Start", "Stop", "Status")]
    [string]$Action = "Status",

    [string]$KeyPath = (Join-Path $HOME ".ssh\id_ed25519")
)

$ErrorActionPreference = "Stop"

$ServerHost = "124.223.26.157"
$SshUser = "ubuntu"
$SshPort = 22
$TunnelTarget = "$SshUser@$ServerHost"

$TunnelSpecs = @(
    [pscustomobject]@{
        Name = "MySQL"
        LocalPort = 13306
        RemotePort = 3306
    },
    [pscustomobject]@{
        Name = "Redis"
        LocalPort = 16379
        RemotePort = 6379
    }
)

function Get-SshProcesses {
    Get-CimInstance Win32_Process -Filter "Name = 'ssh.exe'" |
        Where-Object {
            $_.CommandLine -and
            $_.CommandLine.Contains($TunnelTarget)
        }
}

function Get-TunnelProcesses([object]$Spec) {
    $forward = "-L $($Spec.LocalPort):127.0.0.1:$($Spec.RemotePort)"
    Get-SshProcesses | Where-Object {
        $_.CommandLine.Contains($forward)
    }
}

function Show-Status {
    foreach ($spec in $TunnelSpecs) {
        $processes = @(Get-TunnelProcesses $spec)
        if ($processes.Count -gt 0) {
            $pids = ($processes | ForEach-Object { $_.ProcessId }) -join ", "
            Write-Host "$($spec.Name): running (PID $pids), local port $($spec.LocalPort)"
        }
        else {
            Write-Host "$($spec.Name): stopped, local port $($spec.LocalPort)"
        }
    }
}

function Start-Tunnels {
    if (-not (Test-Path -LiteralPath $KeyPath -PathType Leaf)) {
        throw "SSH key not found: $KeyPath"
    }

    $ssh = Get-Command ssh.exe -ErrorAction Stop

    foreach ($spec in $TunnelSpecs) {
        if (@(Get-TunnelProcesses $spec).Count -gt 0) {
            Write-Host "$($spec.Name) tunnel is already running."
            continue
        }

        $arguments = @(
            "-o", "ExitOnForwardFailure=yes",
            "-o", "ServerAliveInterval=60",
            "-o", "ServerAliveCountMax=3",
            "-N",
            "-L", "$($spec.LocalPort):127.0.0.1:$($spec.RemotePort)",
            "-i", $KeyPath,
            "-p", $SshPort,
            $TunnelTarget
        )

        Start-Process -FilePath $ssh.Source `
            -ArgumentList $arguments `
            -WindowStyle Hidden | Out-Null

        Write-Host "$($spec.Name) tunnel started on local port $($spec.LocalPort)."
    }
}

function Stop-Tunnels {
    $processIds = @(
        foreach ($spec in $TunnelSpecs) {
            Get-TunnelProcesses $spec | ForEach-Object { $_.ProcessId }
        }
    ) | Sort-Object -Unique

    if ($processIds.Count -eq 0) {
        Write-Host "No project SSH tunnels are running."
        return
    }

    foreach ($processId in $processIds) {
        Stop-Process -Id $processId -Force
        Write-Host "Stopped SSH tunnel process $processId."
    }
}

switch ($Action) {
    "Start" {
        Start-Tunnels
        Start-Sleep -Seconds 1
        Show-Status
    }
    "Stop" {
        Stop-Tunnels
        Show-Status
    }
    "Status" {
        Show-Status
    }
}
