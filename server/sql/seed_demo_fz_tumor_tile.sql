SET NAMES utf8mb4;

SET @code = CONVERT('DEMO-FZ-TUMOR' USING utf8mb4) COLLATE utf8mb4_unicode_ci;
SET @old_id = (SELECT activity_id FROM yc_activity WHERE activity_code = @code LIMIT 1);

DELETE FROM yc_apply_field WHERE activity_id = @old_id;
DELETE FROM yc_apply_channel WHERE activity_id = @old_id;
DELETE FROM yc_activity_grid WHERE activity_id = @old_id;
DELETE FROM yc_grid_bottom WHERE activity_id = @old_id;
DELETE FROM yc_activity_config WHERE activity_id = @old_id;
DELETE FROM yc_activity WHERE activity_id = @old_id;

INSERT INTO yc_activity (
  activity_code, activity_name, cover_url, start_time, end_time,
  province, city, address, third_party_url, is_show, is_hot, is_home,
  register_count, visit_count, view_count, del_flag, create_by, create_time, remark
) VALUES (
  @code,
  '福州中医医联体肿瘤早筛早诊早治培训班',
  'http://mpjoy.oss-cn-beijing.aliyuncs.com/20251205/2afa8a40108f4deeba9cd1b82f5a0eef.jpg',
  '2025-12-05 08:00:00',
  '2025-12-07 18:00:00',
  '福建省',
  '福州市',
  '福州市中医院五四北院区门急诊综合楼五楼学术报告厅',
  '',
  '1', '1', '1', 0, 0, 0, '0', 'admin', NOW(),
  '按参考站福州中医肿瘤培训班首页结构配置的浅色 Tile 示例会议'
);

SET @aid = LAST_INSERT_ID();

INSERT INTO yc_activity_config (
  activity_id, mp_show, home_banner, hot_show, show_countdown, countdown_style,
  show_register_count, hotel_need_register, live_need_register, register_show_live,
  register_show_hotel, hotel_once, cancel_register_cancel_hotel, login_sms,
  register_force_mobile, grid_template, qr_url, mobile_template, mobile_theme_color,
  mobile_background_url, mobile_blocks_json, mobile_notice,
  audio_url, audio_autoplay, audio_loop,
  footer_enabled, footer_text, footer_company, footer_logo_url, footer_link_url,
  create_by, create_time
) VALUES (
  @aid, '1', '1', '1', '0', 'digital',
  '0', '0', '0', '0',
  '0', '0', '0', '0',
  '0', 'tile',
  CONCAT('http://localhost:9090/#/pages/meeting/home?activityId=', @aid),
  'standard', '#f6f6f6',
  '', '[]', '',
  '', '0', '1',
  '0', '', '', '', '',
  'admin', NOW()
);

INSERT INTO yc_activity_grid (
  activity_id, title, icon_type, icon_key, icon_url, link_type, module_key,
  external_url, content, content_type, content_url, sort_order,
  tile_row, tile_col, tile_row_span, tile_col_span, status, del_flag,
  create_by, create_time, remark
) VALUES
(@aid, '会议简介', 'image', '', 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20240813/55509227804547cda385095cd7487d13.png',
 'content', '', '',
 '【会议简介】
会议名称：福州中医医联体肿瘤早筛早诊早治培训班
时间：2025年12月05日-07日
地点：福州市中医院五四北院区门急诊综合楼五楼学术报告厅 + 慕鑫服务直播平台
主办单位：福州市中医院
项目编号：省级继续医学教育项目 20251801018

本次培训聚焦肿瘤早筛、早诊、早治，面向医联体相关医务人员开展专题学习。',
 'text', '', 1, 1, 1, 1, 2, '1', '0', 'admin', NOW(),
 '{"bg":"linear-gradient(to right, rgb(240, 98, 146), rgb(194, 24, 91))"}'),

(@aid, '学分须知', 'image', '', 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20240813/77c474ee6ef54ae19dc0b7d981852f56.png',
 'content', '', '',
 '【学分须知】
1. 请按会议安排准时签到、签退，全程参加学习。
2. 学分授予以实际到会及考核情况为准。
3. 如有疑问，请通过「联系我们」咨询会务组。',
 'text', '', 2, 1, 3, 1, 2, '1', '0', 'admin', NOW(),
 '{"bg":"linear-gradient(to right, rgb(124, 77, 255), rgb(159, 122, 234))"}'),

(@aid, '注册报名', 'image', '', 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20240801/3b8366227a2d46e59eafd7eb4e561e12.png',
 'module', 'apply', '', '', 'text', '', 3, 1, 5, 1, 2, '1', '0', 'admin', NOW(),
 '{"bg":"linear-gradient(to right, rgb(233, 30, 99), rgb(240, 98, 146))"}'),

(@aid, '大会议程', 'image', '', 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20240801/94aad633035c44779f26c74e6821ba27.png',
 'content', '', '',
 '【大会议程】
12月05日：开幕式与专题讲座
12月06日：肿瘤早筛早诊专题培训与病例讨论
12月07日：早治路径交流与结业考核

具体场次安排以现场通知为准。',
 'text', '', 4, 2, 1, 2, 3, '1', '0', 'admin', NOW(),
 '{"bg":"linear-gradient(to right, rgb(103, 58, 183), rgb(156, 39, 176))"}'),

(@aid, '大会直播', 'image', '', 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20240801/0c8f1951d9ea421b992944d7ff3eb526.png',
 'url', '', 'https://www.example.com/live/demo',
 '', 'text', '', 5, 2, 4, 1, 3, '1', '0', 'admin', NOW(),
 '{"bg":"linear-gradient(to right, rgb(63, 81, 181), rgb(92, 107, 192))"}'),

(@aid, '会后考试', 'image', '', 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20240813/6e732cc92fc44ecc858ebdfd67839ddb.png',
 'content', '', '',
 '【会后考试】
请参会学员在培训结束后，于规定时间内完成在线考试。
考试入口将在会议期间开放，请留意会务通知。',
 'text', '', 6, 3, 4, 1, 3, '1', '0', 'admin', NOW(),
 '{"bg":"linear-gradient(to right, rgb(0, 150, 136), rgb(77, 182, 172))"}'),

(@aid, '联系我们', 'image', '', 'http://mpjoy.oss-cn-beijing.aliyuncs.com/20240813/bef9a9167f7b495b8353f0cec4a167bf.png',
 'content', '', '',
 '【联系我们】
会务咨询：0591-00000000
主办单位：福州市中医院
工作时间：工作日 09:00-18:00',
 'text', '', 7, 4, 1, 1, 3, '1', '0', 'admin', NOW(),
 '{"bg":"linear-gradient(to right, rgb(76, 175, 80), rgb(129, 205, 190))"}');

INSERT INTO yc_apply_channel (
  activity_id, channel_name, is_main, parent_id, sort_order, price_type, price,
  quota, deadline, need_invite, need_audit, need_invoice, visible, sms_notify,
  del_flag, create_by, create_time, remark
) VALUES (
  @aid, '福州中医医联体肿瘤早筛早诊早治培训班', '1', 0, 1, 'free', 0, 0, '2026-12-31 23:59:59',
  '0', '0', '0', '1', '0', '0', 'admin', NOW(), '福州肿瘤培训班报名通道'
);
SET @cid = LAST_INSERT_ID();

INSERT INTO yc_apply_field (
  channel_id, activity_id, field_scope, field_key, field_name, field_type,
  placeholder, options_json, required_flag, enabled_flag, sort_order, del_flag,
  create_by, create_time
) VALUES
(@cid, @aid, 'standard', 'name', '姓名', 'input', '姓名', NULL, '1', '1', 1, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'mobile', '手机号', 'input', '手机号', NULL, '1', '1', 2, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'attendType', '参会形式', 'select', '参会形式', '["现场参会","线上参会"]', '1', '1', 3, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'gender', '性别', 'radio', '性别', '["男","女"]', '1', '1', 4, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'region', '省市区', 'system', '省市区', NULL, '1', '1', 5, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'idCard', '身份证', 'input', '身份证', NULL, '1', '1', 6, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'age', '年龄', 'input', '年龄', NULL, '1', '1', 7, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'company', '单位', 'input', '单位', NULL, '1', '1', 8, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'department', '科室', 'input', '科室', NULL, '1', '1', 9, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'position', '职务', 'input', '职务', NULL, '1', '1', 10, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'hotel', '是否预定酒店', 'system', '是否预定酒店', '["是","否"]', '0', '0', 100, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'grassroots', '是否来自基层', 'input', NULL, NULL, '0', '0', 101, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'westProvince', '是否隶属西部十二省', 'input', NULL, NULL, '0', '0', 102, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'email', '邮箱', 'input', NULL, NULL, '0', '0', 103, '0', 'admin', NOW()),
(@cid, @aid, 'standard', 'title', '职称', 'input', '请输入职称', NULL, '0', '0', 104, '0', 'admin', NOW());

SELECT @aid AS activity_id, @code AS activity_code;
