SET NAMES utf8mb4;

-- 将 DEMO 会议报名通道对齐管理端 standard/extend 模型
UPDATE yc_apply_channel
SET channel_name = '福州中医医联体肿瘤早筛早诊早治培训班',
    deadline = '2026-12-31 23:59:59',
    visible = '1',
    remark = '参考站报名页示例通道'
WHERE activity_id = 13;

SET @cid = (SELECT channel_id FROM yc_apply_channel WHERE activity_id = 13 ORDER BY channel_id LIMIT 1);

DELETE FROM yc_apply_field WHERE activity_id = 13;

INSERT INTO yc_apply_field (
  channel_id, activity_id, field_scope, field_key, field_name, field_type,
  placeholder, options_json, required_flag, enabled_flag, sort_order, del_flag,
  create_by, create_time
) VALUES
-- 手机验证区
(@cid, 13, 'standard', 'name', '姓名', 'input', '姓名', NULL, '1', '1', 1, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'mobile', '手机号', 'input', '手机号', NULL, '1', '1', 2, '0', 'admin', NOW()),
-- 报名信息区（参考站字段）
(@cid, 13, 'standard', 'attendType', '参会形式', 'select', '参会形式', '["现场参会","线上参会"]', '1', '1', 3, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'gender', '性别', 'radio', '性别', '["男","女"]', '1', '1', 4, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'region', '省市区', 'system', '省市区', NULL, '1', '1', 5, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'idCard', '身份证', 'input', '身份证', NULL, '1', '1', 6, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'age', '年龄', 'input', '年龄', NULL, '1', '1', 7, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'company', '单位', 'input', '单位', NULL, '1', '1', 8, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'department', '科室', 'input', '科室', NULL, '1', '1', 9, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'position', '职务', 'input', '职务', NULL, '1', '1', 10, '0', 'admin', NOW()),
-- 其余标准字段默认停用，后台可随时启用
(@cid, 13, 'standard', 'hotel', '是否预定酒店', 'system', '是否预定酒店', '["是","否"]', '0', '0', 100, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'grassroots', '是否来自基层', 'input', NULL, NULL, '0', '0', 101, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'westProvince', '是否隶属西部十二省', 'input', NULL, NULL, '0', '0', 102, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'email', '邮箱', 'input', NULL, NULL, '0', '0', 103, '0', 'admin', NOW()),
(@cid, 13, 'standard', 'title', '职称', 'input', '请输入职称', NULL, '0', '0', 104, '0', 'admin', NOW());

SELECT c.channel_id, c.channel_name, c.deadline, f.field_scope, f.field_key, f.field_name, f.field_type,
       f.required_flag, f.enabled_flag, f.sort_order
FROM yc_apply_channel c
LEFT JOIN yc_apply_field f ON f.channel_id = c.channel_id AND f.del_flag = '0'
WHERE c.activity_id = 13
ORDER BY f.sort_order, f.field_id;
