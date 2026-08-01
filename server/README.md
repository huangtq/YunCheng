# YunCheng Server

基于 RuoYi-Vue 的后端服务，Spring Boot + Spring Security + Redis + JWT。

## 技术栈

- Java 17+
- Spring Boot 4.x
- Spring Security + JWT
- MyBatis / Redis
- Maven 多模块：`ruoyi-admin`、`ruoyi-framework`、`ruoyi-system`、`ruoyi-common`、`ruoyi-quartz`、`ruoyi-generator`

## 内置功能

1. 用户 / 部门 / 岗位 / 菜单 / 角色管理
2. 字典 / 参数 / 通知公告
3. 操作日志 / 登录日志 / 在线用户
4. 定时任务
5. 代码生成
6. 系统接口文档
7. 服务监控 / 缓存监控 / 连接池监视

## 本地运行

1. 创建数据库，导入 `sql` 目录下的脚本
2. 修改 `ruoyi-admin/src/main/resources/application-druid.yml` 中的数据库与 Redis 配置
3. 启动：

```bash
# Windows
ry.bat

# Linux / macOS
./ry.sh
```

或在 IDE 中运行 `ruoyi-admin` 模块的启动类。默认端口：`8080`。
