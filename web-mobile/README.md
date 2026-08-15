# YunCheng Meeting H5

基于 uni-app（Vue3）的会议服务号 H5，一套代码可编译到 H5。

## 技术栈

- uni-app
- Vue 3
- 微信服务号网页授权

## 功能

- 当前会议、历史会议和底部导航
- 会议首页、封面和倒计时
- 九宫格、底部栏和会议模块只读展示
- 标准会议模板、宣传图热点模板
- 会议议程、嘉宾、会场、导航、展商、餐票、酒店信息
- 参会报名、动态表单、绑定手机号、我的报名
- 微信 OAuth；本地开发支持 mock 授权

本项目不提供云珵管理员登录、工作台和个人中心。浏览会议不需要管理员账号，提交报名时才需要绑定手机号。

## 路由说明

H5 使用 **history** 模式，基路径为 `/h5/`。

正式入口示例：

- 会议列表：`https://yunchengmice.cn/h5/pages/meeting/index`
- 会议首页：`https://yunchengmice.cn/h5/pages/meeting/home?activityId=13`
- 分享落地页（带中文 meta）：`https://yunchengmice.cn/prod-api/portal/wx/share?activityId=13`

旧 hash 链接（`/h5/#/pages/...`）打开后会自动跳转到 history 地址。

## 本地运行

本项目使用 **HBuilderX**：

1. 用 HBuilderX 打开本目录
2. 修改 `config.js` 中的 `baseUrl`，指向本地或正式后端
3. 菜单「运行」→ 运行到浏览器

平台入口：

`http://localhost:9090/h5/pages/meeting/index`

会议详情入口：

`http://localhost:9090/h5/pages/meeting/home?activityId=2`

请先启动 `server` 后端，再运行 H5。

## 部署注意（Nginx）

history 模式必须把 `/h5/` 下的前端路由回退到 `index.html`，示例见 `deploy/nginx-h5.conf.example`。
