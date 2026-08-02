# YunCheng Server

基于 RuoYi-Vue 的后端服务，Spring Boot + Spring Security + Redis + JWT。

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

或在 IDE 中运行 `ruoyi-admin` 模块的启动类。默认端口：`8080`。

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
`ruoyi-admin/src/main/resources/application-local.yml` 中填写服务器
MySQL 用户 `ruoyi` 的密码。该文件已加入 `.gitignore`，不要将密码写入其他
受 Git 管理的配置文件。

开发启动顺序：

1. 启动 SSH 隧道。
2. 确认 `application-local.yml` 中的数据库密码正确。
3. 在 IDE 中运行 `ruoyi-admin` 的启动类。
4. 停止开发时关闭应用，再执行隧道关闭命令。

## 在 Cursor 的 Run and Debug 中启动

项目根目录下的 `.vscode/launch.json` 已配置
`Start RuoYi Server`，`.vscode/tasks.json` 已配置隧道的启动与关闭任务。
这些文件属于本机 IDE 配置，已被根目录 `.gitignore` 忽略。

使用方式：

1. 确保 `application-local.yml` 中已填写 MySQL 密码。
2. 打开 Cursor 左侧的 **Run and Debug** 面板。
3. 选择 `Start RuoYi Server`。
4. 点击绿色启动按钮或按 `F5`。

启动配置会先自动启动 SSH 隧道，停止调试会话后自动关闭由本项目创建的
SSH 隧道。
