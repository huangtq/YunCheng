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

本项目不提供若依管理员登录、工作台和个人中心。浏览会议不需要管理员账号，提交报名时才需要绑定手机号。

## 本地运行

本项目使用 **HBuilderX**：

1. 用 HBuilderX 打开本目录
2. 修改 `config.js` 中的 `baseUrl`，指向本地或正式后端
3. 菜单「运行」→ 运行到浏览器

平台入口：

`http://localhost:9090/#/pages/meeting/index`

会议详情入口：

`http://localhost:9090/#/pages/meeting/home?activityId=2`

请先启动 `server` 后端，再运行 H5。
