# YunCheng Server

> Production deployment: see [`../docs/production-deployment.md`](../docs/production-deployment.md). Keep the production external configuration unchanged during releases.

云珵会议管理平台后端服务，Spring Boot + Spring Security + Redis + JWT。

## 技术栈

- Java 17+
- Spring Boot 4.x
- Spring Security + JWT
- MyBatis / Redis
- Maven 多模块：`ruoyi-admin`、`ruoyi-framework`、`ruoyi-system`、`ruoyi-common`、`ruoyi-quartz`

## 内置功能

1. 用户 / 部门 / 岗位 / 菜单 / 角色管理
2. 字典 / 参数 / 通知公告
3. 操作日志 / 登录日志 / 在线用户
4. 定时任务
5. 服务监控 / 缓存监控 / 连接池监视

## 本地运行

1. 创建数据库，导入 `sql` 目录下的脚本
2. 远程 MySQL / Redis 的连接方式见下方 SSH 隧道流程
3. 启动：

```bash
# Windows
ry.bat

# Linux / macOS
./ry.sh
```

或在 Codex 终端执行项目提供的 `start-dev.cmd`。默认端口：`8080`。

## 本地开发：连接远端 MySQL 和 Redis

本地开发通过 SSH 隧道连接服务器上的数据库服务：

- MySQL：本机 `127.0.0.1:13306` 转发到服务器 `127.0.0.1:3306`
- Redis：本机 `127.0.0.1:16379` 转发到服务器 `127.0.0.1:6379`
- 服务器：`ubuntu@124.223.26.157:22`
- SSH 私钥默认路径：`%USERPROFILE%\.ssh\id_ed25519`

在 `server` 目录执行以下命令：

```powershell
# 启动隧道
powershell -ExecutionPolicy Bypass -File .\scripts\ssh-tunnel.ps1 -Action Start

# 查看隧道状态
powershell -ExecutionPolicy Bypass -File .\scripts\ssh-tunnel.ps1 -Action Status

# 关闭隧道
powershell -ExecutionPolicy Bypass -File .\scripts\ssh-tunnel.ps1 -Action Stop
```

首次运行前，在未提交到 Git 的
`ruoyi-admin/src/main/resources/application-local.yml` 中填写服务器数据库密码。
该文件已加入 `.gitignore`，不要将密码写入其他受 Git 管理的配置文件。

开发启动方式：

1. 确认 `application-local.yml` 中的数据库密码正确。
2. 在 Codex 终端执行 `server\\start-dev.cmd`，脚本会自动启动 SSH 隧道、安装模块并运行后端。
3. 停止开发时关闭脚本，脚本会自动关闭由它创建的 SSH 隧道。

## 使用 Codex 终端启动

项目提供 `server\\start-dev.cmd`，用于在 Codex 终端启动后端开发服务。

使用方式：

1. 确保 `application-local.yml` 中已填写 MySQL 密码。
2. 在项目根目录的 Codex 终端执行 `server\\start-dev.cmd`。
3. 等待终端出现启动成功信息。

脚本会先启动 SSH 隧道，后端进程退出后自动关闭由本项目创建的 SSH 隧道。
