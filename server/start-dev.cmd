@echo off
setlocal

rem Start the same source-based Spring Boot process used by Cursor launch.json.
pushd "%~dp0"

set "SPRING_PROFILES_ACTIVE=druid,local"
set "SPRING_CONFIG_ADDITIONAL_LOCATION=%CD%\ruoyi-admin\src\main\resources\"
set "RUOYI_TEST_RESOURCE_ENABLED=true"

echo [1/3] Starting project SSH tunnels...
powershell -NoProfile -ExecutionPolicy Bypass -File ".\scripts\ssh-tunnel.ps1" -Action Start
if errorlevel 1 (
    echo SSH tunnel startup failed. The backend may not be able to connect to MySQL or Redis.
    popd
    exit /b 1
)

echo [2/3] Installing source modules...
call mvn.cmd -pl ruoyi-admin -am -DskipTests install
if errorlevel 1 (
    echo Source module installation failed. SSH tunnels will be stopped.
    powershell -NoProfile -ExecutionPolicy Bypass -File ".\scripts\ssh-tunnel.ps1" -Action Stop
    popd
    exit /b 1
)

echo [3/3] Starting YunCheng with Spring Boot DevTools enabled...
pushd ".\ruoyi-admin"
call mvn.cmd spring-boot:run ^
    -Dspring-boot.run.main-class=com.ruoyi.RuoYiApplication ^
    -Dspring-boot.run.jvmArguments=-Dfile.encoding=UTF-8 ^
    -Dspring-boot.run.fork=true
set "exitCode=%ERRORLEVEL%"
popd

echo Stopping project SSH tunnels...
powershell -NoProfile -ExecutionPolicy Bypass -File ".\scripts\ssh-tunnel.ps1" -Action Stop
popd
exit /b %exitCode%
