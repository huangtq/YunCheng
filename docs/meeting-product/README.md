# 会议产品需求文档集

本目录将“移动端首页编排”和会议服务能力按产品边界拆分，替代单体 PRD。阅读与实施入口如下：

| 文档 | 说明 | 优先级 |
| --- | --- | --- |
| [00-产品总览](00-product-overview.md) | 愿景、用户旅程、产品边界与总优先级 | 全局 |
| [01-移动端首页编排](01-mobile-home-composer.md) | 首页模式、区块、导航、预览、发布 | P0 |
| [02-报名与我的参会](02-registration-and-my-attendance.md) | 票种、报名、审核、个人服务中枢 | P0/P1 |
| [03-日程嘉宾与导航](03-agenda-guests-and-navigation.md) | 日程、嘉宾、会场、POI 导航 | P0/P1 |
| [04-现场服务](04-on-site-services.md) | 凭证、签到、酒店、餐券与核销 | P1 |
| [05-内容通知与展商](05-content-notices-and-exhibitors.md) | 资料、通知、回放、反馈、展商 | P0/P2 |
| [06-运营与数据治理](06-operations-and-governance.md) | 运营驾驶舱、权限、指标、数据质量 | P1 |
| [07-服务端协议与迁移](07-platform-api-and-migration.md) | API、数据模型、兼容、安全、分期 | P0 |

## 使用原则

- 先确认 [00-产品总览](00-product-overview.md) 的用户目标和 P0 范围，再进入具体功能文档。
- 每个功能文档都包含移动端、Web 管理端、服务端规则与验收要点。
- 业务规则以服务端为准；Web 和移动端只负责配置、呈现和交互。
- 本文档集描述目标产品，不代表全部功能必须一次上线；以 [07-服务端协议与迁移](07-platform-api-and-migration.md) 的分期计划为实施边界。
