-- Local demo data copied from the public reference-page structure.
-- UTF-8 text is stored through hex literals so this file is safe on Windows.

set @old_a3 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-STANDARD');
set @old_a4 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-IMAGE');
set @old_a5 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-FILES');

delete from yc_apply_field where activity_id in (@old_a3, @old_a4, @old_a5);
delete from yc_apply_channel where activity_id in (@old_a3, @old_a4, @old_a5);
delete from yc_activity_grid where activity_id in (@old_a3, @old_a4, @old_a5);
delete from yc_grid_bottom where activity_id in (@old_a3, @old_a4, @old_a5);
delete from yc_activity_config where activity_id in (@old_a3, @old_a4, @old_a5);
delete from yc_activity where activity_id in (@old_a3, @old_a4, @old_a5);

insert into yc_activity (
  activity_code, activity_name, cover_url, start_time, end_time,
  province, city, address, third_party_url, is_show, is_hot, is_home,
  register_count, visit_count, view_count, del_flag, create_by, create_time, remark
) values
(
  'DEMO-MOBILE-STANDARD', convert(0xE7A4BAE4BE8BE4BC9AE8AEAEC2B7E6A087E58786E6A8A1E69DBF using utf8mb4),
  '/reference/home/meeting-3-cover.jpg', '2026-09-01 09:00:00', '2026-09-03 18:00:00',
  convert(0xE7A68FE5BBBAE79C81 using utf8mb4), convert(0xE6B389E5B79EE5B882 using utf8mb4),
  convert(0xE6B389E5B79EE4BC9AE5B195E4B8ADE5BF83 using utf8mb4), '', '1', '1', '1',
  0, 0, 0, '0', 'demo', sysdate(), convert(0xE7A7BBE58AA8E7ABAFE6A087E58786E6A8A1E69DBFE6B58BE8AF95E4BC9AE8AEAE using utf8mb4)
),
(
  'DEMO-MOBILE-IMAGE', convert(0xE7A4BAE4BE8BE4BC9AE8AEAEC2B7E5AEA3E4BCA0E59BBEE783ADE782B9E6A8A1E69DBF using utf8mb4),
  '/reference/home/meeting-36113-cover.jpg', '2026-09-10 09:00:00', '2026-09-12 18:00:00',
  convert(0xE7A68FE5BBBAE79C81 using utf8mb4), convert(0xE58EA6E997A8E5B882 using utf8mb4),
  convert(0xE58EA6E997A8E59BBDE99985E4BC9AE8AEAEE4B8ADE5BF83 using utf8mb4), '', '1', '1', '0',
  0, 0, 0, '0', 'demo', sysdate(), convert(0xE7A7BBE58AA8E7ABAFE5AEA3E4BCA0E59BBEE783ADE782B9E6A8A1E69DBFE6B58BE8AF95E4BC9AE8AEAE using utf8mb4)
),
(
  'DEMO-MOBILE-FILES', convert(0xE7A4BAE4BE8BE4BC9AE8AEAEC2B7E4BC9AE8AEAEE9A1BBE79FA5E4B88EE69687E4BBB6 using utf8mb4),
  '/reference/home/meeting-39047-cover.jpg', '2026-09-20 09:00:00', '2026-09-22 18:00:00',
  convert(0xE7A68FE5BBBAE79C81 using utf8mb4), convert(0xE7A68FE5B79EE5B882 using utf8mb4),
  convert(0xE7A68FE5B79EE6B5B7E5B3A1E59BBDE99985E4BC9AE5B195E4B8ADE5BF83 using utf8mb4), '', '1', '0', '0',
  0, 0, 0, '0', 'demo', sysdate(), convert(0xE7A7BBE58AA8E7ABAFE69687E4BBB6E4B88BE8BDBDE4B88EE68AA5E5908DE6B58BE8AF95E4BC9AE8AEAE using utf8mb4)
);

set @a3 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-STANDARD');
set @a4 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-IMAGE');
set @a5 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-FILES');

insert into yc_activity_config (
  activity_id, mp_show, home_banner, hot_show, show_countdown, countdown_style,
  show_register_count, hotel_need_register, live_need_register, register_show_live,
  register_show_hotel, hotel_once, cancel_register_cancel_hotel, login_sms,
  register_force_mobile, grid_template, qr_url, mobile_template, mobile_theme_color,
  mobile_background_url, mobile_blocks_json, mobile_notice, create_by, create_time
) values
(
  @a3, '1', '1', '1', '1', 'classic', '1', '0', '0', '0', '0', '0', '0', '0',
  '1', 'grid3x3', concat('http://localhost:9090/#/pages/meeting/home?activityId=', @a3),
  'standard', '#2878f0', '', '[]',
  convert(0xE6ACA2E8BF8EE8BF9BE585A5E6A087E58786E6A8A1E69DBFE7A4BAE4BE8BE4BC9AE8AEAEEFBC8CE58FAFE4BB8EE4B99DE5AEABE6A0BCE69FA5E79C8BE4BC9AE8AEAEE69C8DE58AA1E38082 using utf8mb4),
  'demo', sysdate()
),
(
  @a4, '1', '0', '1', '1', 'simple', '0', '0', '0', '0', '0', '0', '0', '0',
  '1', 'grid3x3', concat('http://localhost:9090/#/pages/meeting/home?activityId=', @a4),
  'image-map', '#1f6feb', '/reference/meeting-36113/detail-background.jpg',
  0x5B7B227469746C65223A22E4BC9AE8AEAEE4BB8BE7BB8D222C226C656674223A372C22746F70223A31382C227769647468223A32352C22686569676874223A31322C226C696E6B54797065223A22636F6E74656E74222C22636F6E74656E74223A22E69CACE4BC9AE8AEAEE794A8E4BA8EE9AA8CE8AF81E5AEA3E4BCA0E59BBEE783ADE782B9E5B883E5B180EFBC8CE782B9E587BBE59BBEE78987E4B88AE79A84E88F9CE58D95E8BF9BE585A5E5AFB9E5BA94E58A9FE883BDE38082227D2C7B227469746C65223A22E4BC9AE8AEAEE8AEAEE7A88B222C226C656674223A34302C22746F70223A31382C227769647468223A32352C22686569676874223A31322C226C696E6B54797065223A226D6F64756C65222C226D6F64756C654B6579223A227363686564756C65227D2C7B227469746C65223A22E58F82E4BC9AE68AA5E5908D222C226C656674223A36372C22746F70223A31382C227769647468223A32352C22686569676874223A31322C226C696E6B54797065223A226D6F64756C65222C226D6F64756C654B6579223A226170706C79227D2C7B227469746C65223A22E59889E5AEBEE58897E8A1A8222C226C656674223A372C22746F70223A33342C227769647468223A32352C22686569676874223A31322C226C696E6B54797065223A226D6F64756C65222C226D6F64756C654B6579223A226775657374227D2C7B227469746C65223A22E4BC9AE59CBAE5AFBCE888AA222C226C656674223A36372C22746F70223A33342C227769647468223A32352C22686569676874223A31322C226C696E6B54797065223A226D6F64756C65222C226D6F64756C654B6579223A2276656E7565227D2C7B227469746C65223A22E8B584E69699E9A284E8A788222C226C656674223A32352C22746F70223A35312C227769647468223A35302C22686569676874223A31342C226C696E6B54797065223A2275726C222C2265787465726E616C55726C223A22687474703A2F2F6C6F63616C686F73743A383038302F7265666572656E63652F6D656574696E672D33363131332F6167656E64612E706E67227D5D,
  convert(0xE69CACE4BC9AE8AEAEE4BDBFE794A8E5AEA3E4BCA0E59BBEE783ADE782B9E5B883E5B180EFBC8CE782B9E587BBE59BBEE78987E4B88AE79A84E88F9CE58D95E8BF9BE585A5E5AFB9E5BA94E58A9FE883BDE38082 using utf8mb4),
  'demo', sysdate()
),
(
  @a5, '1', '0', '0', '1', 'digital', '0', '0', '0', '0', '0', '0', '0', '0',
  '1', 'grid3x3', concat('http://localhost:9090/#/pages/meeting/home?activityId=', @a5),
  'image-map', '#0b4f9c', '/reference/meeting-39047/detail-background.jpg',
  0x5B7B227469746C65223A22E69687E4BBB6E4B88BE8BDBD222C226C656674223A372C22746F70223A31382C227769647468223A38362C22686569676874223A382C226C696E6B54797065223A2275726C222C2265787465726E616C55726C223A22687474703A2F2F6C6F63616C686F73743A383038302F7265666572656E63652F6D656574696E672D33393034372F6167656E64612E706466227D2C7B227469746C65223A22E4BC9AE8AEAEE9A1BBE79FA5222C226C656674223A372C22746F70223A33312C227769647468223A32352C22686569676874223A31342C226C696E6B54797065223A2275726C222C2265787465726E616C55726C223A22687474703A2F2F6C6F63616C686F73743A383038302F7265666572656E63652F6D656574696E672D33393034372F6E6F746963652E706466227D2C7B227469746C65223A22E5A4A7E4BC9AE697A5E7A88B222C226C656674223A36392C22746F70223A33312C227769647468223A32352C22686569676874223A31342C226C696E6B54797065223A226D6F64756C65222C226D6F64756C654B6579223A227363686564756C65227D2C7B227469746C65223A22E58F82E4BC9AE68AA5E5908D222C226C656674223A32372C22746F70223A34322C227769647468223A34362C22686569676874223A32342C226C696E6B54797065223A226D6F64756C65222C226D6F64756C654B6579223A226170706C79227D2C7B227469746C65223A22E4BC9AE59CBAE68C87E5BC95222C226C656674223A32372C22746F70223A36382C227769647468223A34362C226C696E6B54797065223A226D6F64756C65222C226D6F64756C654B6579223A226E6176227D2C7B227469746C65223A22E88194E7B3BBE68891E4BBAC222C226C656674223A37332C22746F70223A36382C227769647468223A32322C226C696E6B54797065223A22636F6E74656E74222C22636F6E74656E74223A22E4BC9AE58AA1E694AFE68C81EFBC9AE8AFB7E9809AE8BF87E5908EE58FB0E9858DE7BDAEE88194E7B3BBE696B9E5BC8FE38082227D5D,
  convert(0xE69CACE4BC9AE8AEAEE794A8E4BA8EE9AA8CE8AF81E69687E4BBB6E4B88BE8BDBDE38081E68AA5E5908DE5928CE887AAE5AE9AE4B989E783ADE782B9E5B883E5B180E38082 using utf8mb4),
  'demo', sysdate()
);

insert into yc_activity_grid (
  activity_id, title, icon_type, icon_key, icon_url, link_type, module_key,
  external_url, content, sort_order, status, del_flag, create_by, create_time
) values
(@a3, convert(0xE4BC9AE8AEAEE8AEAEE7A88B using utf8mb4), 'icon', 'calendar', '', 'module', 'schedule', '', '', 1, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE59889E5AEBEE58897E8A1A8 using utf8mb4), 'icon', 'guest', '', 'module', 'guest', '', '', 2, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE58F82E4BC9AE68AA5E5908D using utf8mb4), 'icon', 'apply', '', 'module', 'apply', '', '', 3, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE4BC9AE59CBAE5AFBCE888AA using utf8mb4), 'icon', 'venue', '', 'module', 'venue', '', '', 4, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE4BC9AE8AEAEE5AFBCE888AA using utf8mb4), 'icon', 'nav', '', 'module', 'nav', '', '', 5, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE5B195E59586E5908DE5BD95 using utf8mb4), 'icon', 'shop', '', 'module', 'exhibitor', '', '', 6, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE9A490E7A5A8E69C8DE58AA1 using utf8mb4), 'icon', 'meal', '', 'module', 'meal', '', '', 7, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE98592E5BA97E4BFA1E681AF using utf8mb4), 'icon', 'hotel', '', 'module', 'hotel', '', '', 8, '1', '0', 'demo', sysdate());

insert into yc_grid_bottom (
  activity_id, bottom_name, bottom_type, link_url, module_key, phone, icon_url,
  sort_order, status, del_flag, create_by, create_time
) values
(@a3, convert(0xE9A696E9A1B5 using utf8mb4), 'module', '', 'schedule', '', '', 1, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE68AA5E5908D using utf8mb4), 'module', '', 'apply', '', '', 2, '1', '0', 'demo', sysdate()),
(@a3, convert(0xE68891E79A84E68AA5E5908D using utf8mb4), 'module', '', 'apply', '', '', 3, '1', '0', 'demo', sysdate());

insert into yc_apply_channel (
  activity_id, channel_name, is_main, parent_id, sort_order, price_type, price,
  quota, need_invite, need_audit, need_invoice, visible, sms_notify, del_flag,
  create_by, create_time, remark
) values (
  @a3, convert(0xE699AEE9809AE58F82E4BC9AE68AA5E5908D using utf8mb4), '1', 0, 1, 'free', 0, 0, '0', '0', '0', '1', '0', '0',
  'demo', sysdate(), convert(0xE6A087E58786E6A8A1E69DBFE6B58BE8AF95E68AA5E5908DE9809AE98193 using utf8mb4)
);
set @c3 = last_insert_id();

insert into yc_apply_field (
  channel_id, activity_id, field_scope, field_key, field_name, field_type,
  placeholder, options_json, required_flag, enabled_flag, sort_order, del_flag,
  create_by, create_time
) values
(@c3, @a3, 'standard', 'name', convert(0xE5A793E5908D using utf8mb4), 'input', convert(0xE8AFB7E8BE93E585A5E5A793E5908D using utf8mb4), '', '1', '1', 1, '0', 'demo', sysdate()),
(@c3, @a3, 'standard', 'gender', convert(0xE680A7E588AB using utf8mb4), 'radio', convert(0xE8AFB7E98089E68B9EE680A7E588AB using utf8mb4), convert(0x5B22E794B7222C22E5A5B3225D using utf8mb4), '1', '1', 2, '0', 'demo', sysdate()),
(@c3, @a3, 'standard', 'company', convert(0xE58D95E4BD8D using utf8mb4), 'input', convert(0xE8AFB7E8BE93E585A5E58D95E4BD8D using utf8mb4), '', '1', '1', 3, '0', 'demo', sysdate()),
(@c3, @a3, 'standard', 'position', convert(0xE8818CE58AA1 using utf8mb4), 'input', convert(0xE8AFB7E8BE93E585A5E8818CE58AA1 using utf8mb4), '', '0', '1', 4, '0', 'demo', sysdate());

insert into yc_apply_channel (
  activity_id, channel_name, is_main, parent_id, sort_order, price_type, price,
  quota, need_invite, need_audit, need_invoice, visible, sms_notify, del_flag,
  create_by, create_time, remark
) values
(@a4, convert(0xE5AEA3E4BCA0E59BBEE4BC9AE8AEAEE68AA5E5908D using utf8mb4), '1', 0, 1, 'free', 0, 0, '0', '0', '0', '1', '0', '0', 'demo', sysdate(), convert(0xE783ADE782B9E6A8A1E69DBFE6B58BE8AF95E68AA5E5908DE9809AE98193 using utf8mb4)),
(@a5, convert(0xE69687E4BBB6E4BC9AE8AEAEE68AA5E5908D using utf8mb4), '1', 0, 1, 'free', 0, 0, '0', '0', '0', '1', '0', '0', 'demo', sysdate(), convert(0xE69687E4BBB6E6A8A1E69DBFE6B58BE8AF95E68AA5E5908DE9809AE98193 using utf8mb4));
