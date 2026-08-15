# YunCheng Web

> Production deployment: see [`../docs/production-deployment.md`](../docs/production-deployment.md).

云珵会议管理平台 Web 管理端，Vue3 + Element Plus + Vite。

## 技术栈

- Vue 3
- Element Plus
- Vite
- Pinia
- Vue Router 4
- Axios

## 本地运行

```bash
# 安装依赖
pnpm install

# 启动开发服务（Codex 终端）
.\\start-dev.cmd

# 构建生产包
pnpm build:prod
```

开发环境默认访问：`http://localhost:80`。请先启动 `server` 后端。

## 内置功能

与后端配套，包含会议配置、用户权限、系统监控、日志管理等后台管理能力。
