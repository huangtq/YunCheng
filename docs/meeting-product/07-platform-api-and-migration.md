# 服务端协议、数据模型与迁移

## 1. 统一首页协议

新增：

```text
GET /portal/meeting/home/{activityId}
```

该接口返回活动公开信息、已发布版本、页面区块、用户上下文和能力状态。移动端以此作为唯一首页渲染依据。

```json
{
  "data": {
    "activity": {},
    "version": { "versionId": 1001, "versionNo": 3, "status": "published" },
    "page": { "mode": "standard", "theme": {}, "sections": [], "entryTree": [] },
    "capabilities": {},
    "context": { "stage": "before", "loggedIn": false, "registered": false }
  }
}
```

保留 `/portal/meeting/activity/{activityId}` 和 `/portal/meeting/grid/{activityId}` 供旧客户端使用；新客户端优先使用统一接口。

## 2. 服务端职责

服务端统一负责：已发布版本读取、会议阶段、入口可见性、资格、模块数据存在性、入口状态、入口树组装、Tile/图片热区校验、权限、公开字段过滤和协议降级。

Web 负责编辑与预览，H5 负责渲染与交互；两端都不得自行推断库存、报名资格或模块可用性。

## 3. 建议数据模型

### 首页版本

新增 `yc_activity_home_version`：`version_id`、`activity_id`、`version_no`、`status`、`schema_version`、`page_json`、发布备注、创建/发布信息、删除标记。

一期可将区块全部保存进 `page_json`，但 JSON 必须版本化；后续需要协作和精细统计时再拆 `yc_activity_home_section`。

### 建议新增实体

- `yc_attendee_pass`、`yc_checkin_log`；
- `yc_user_schedule_favorite`、`yc_schedule_change_log`；
- `yc_user_meal_coupon`、`yc_meal_redeem_log`、`yc_meal_point`；
- `yc_meeting_notice`、`yc_user_notice_read`；
- `yc_meeting_content`、`yc_meeting_content_attachment`（通用内容页和受控附件）；
- 可选 `yc_user_service_task`，聚合“我的待办”。

现有 `yc_activity`、`yc_activity_config`、`yc_activity_grid`、报名、日程、酒店、餐券和展商表继续复用，详见各功能文档。

## 4. 模块能力注册表

服务端和 Web 使用同一份版本化模块定义：模块名、管理路径、移动端路由、是否需登录、数据检查方法、是否支持角标和空状态。新增模块时不能在多个前端文件手工复制判断。

首批受控模块包括 `apply`、`schedule`、`guest`、`venue`、`nav`、`hotel`、`meal`、`checkin`、`exhibitor`、`notice`、`replay`、`my-attendance`。其中邀请函、通知、须知、组织机构、基本信息、联系信息、酒店说明和学分说明等不应伪装为模块，应使用 `content` 目标类型指向统一内容页；照片直播等第三方服务使用 `external`。未实现或没有真实数据的模块默认隐藏入口，完整边界见 [原生模块目录](08-native-module-catalog.md)。

## 5. 安全

- 外链仅 `http/https`；
- 富文本做 XSS 清洗；
- 页面 JSON 限制大小和 schema；
- 禁止配置脚本、事件属性和任意路由；
- 发布接口要求活动编辑/发布权限；
- 匿名接口只返回公开数据；
- 凭证、核销和状态变更接口需鉴权、幂等和审计。

## 6. 旧数据迁移

| 旧字段/数据 | 新模型映射 |
| --- | --- |
| `gridTemplate=1/5/68/7/71` | `nav-grid` 的列数与图标样式 |
| `gridTemplate=tile` | `nav-grid.layout=tile` |
| `yc_activity_grid` | module/content/external 类型入口 |
| `remark.__gridForm` | 结构化视觉属性 |
| `mobile_blocks_json` | feature 或 free-layout 区块 |
| `status=0` | `enabled=false` |
| `sort_order` | `sort` |

迁移时应从 `yc_activity_grid` 建立入口树：没有父级的项作为根入口；历史中用于展示而没有目标的项保留为分组或禁用项，不能猜测为可跳转链接。`mobile_blocks_json` 的像素或百分比热区需保留原始图片尺寸，转换后统一为版本化图片地图区块。

采用双读单写：新版本存在则读新版本；不存在时实时转换旧数据；新编排只写版本表并保留兼容快照；历史活动完成迁移后旧接口进入只读兼容期。异常时通过生效版本指针回滚，不删除版本。

## 7. 分期落地

### Phase 1

- 统一首页协议、版本表、旧数据转换；
- Web 配置概念梳理、校验、真实预览；
- H5 优先读取新协议；
- 首页、报名状态、日程导航、资料的 P0 闭环。
- 入口树、统一内容页和受控附件。

### Phase 2

- 可视化编排、显示条件、版本差异；
- 凭证签到、餐券、酒店个人服务、通知中心；
- 运营驾驶舱。

### Phase 3

- 自由 Tile 拖拽、自动阶段切换、定时发布；
- 展商互动、回放反馈、数据看板和 A/B 方案。

## 8. 验收

- 统一接口一次返回首页所需公开数据；
- 旧会议无新版本仍正常访问；
- 新旧接口兼容期均可用；
- 无效配置不可发布，旧数据可转换且可回滚；
- 权限、公开范围、幂等和审计均由服务端校验。
