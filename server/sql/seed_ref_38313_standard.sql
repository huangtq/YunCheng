
SET NAMES utf8mb4;

SET @code = CONVERT('REF-38313-ANES' USING utf8mb4) COLLATE utf8mb4_unicode_ci;
SET @old_id = (SELECT activity_id FROM yc_activity WHERE activity_code = @code LIMIT 1);

DELETE FROM yc_apply_field WHERE activity_id = @old_id;
DELETE FROM yc_apply_channel WHERE activity_id = @old_id;
DELETE FROM yc_activity_grid WHERE activity_id = @old_id;
DELETE FROM yc_grid_bottom WHERE activity_id = @old_id;
DELETE FROM yc_activity_config WHERE activity_id = @old_id;
DELETE FROM yc_schedule WHERE activity_id = @old_id;
DELETE FROM yc_topic WHERE activity_id = @old_id;
DELETE FROM yc_venue WHERE activity_id = @old_id;
DELETE FROM yc_activity WHERE activity_id = @old_id;

INSERT INTO yc_activity (
  activity_code, activity_name, cover_url, start_time, end_time,
  province, city, address, third_party_url, is_show, is_hot, is_home,
  register_count, visit_count, view_count, del_flag, create_by, create_time, remark
) VALUES (
  @code, convert(0x32303236E5B9B4E58DB1E9878DE79691E99ABEE79785E4BE8BE4B88EE9BABBE98689E5AE89E585A8E5ADA6E4B9A0E78FAD using utf8mb4),
  '/reference/meeting-38313/cover-banner.jpg',
  '2026-08-07 08:30:00', '2026-08-08 18:00:00',
  convert(0xE7A68FE5BBBAE79C81 using utf8mb4), convert(0xE6B389E5B79EE5B882 using utf8mb4), convert(0xE6B389E5B79EE6B5B7E4B89DE58D9AE4BA9AE59BBDE99985E98592E5BA97 using utf8mb4),
  '', '1', '1', '1', 128, 0, 0, '0', 'demo', NOW(), convert(0xE68C89E58F82E88083E7AB99206D6D2E736369636F6E662E636E2F6D696E69736974652F696E6465782F333833313320E9858DE7BDAEE79A84E6A087E58786E58F8CE58897E4B99DE5AEABE6A0BCE7A4BAE4BE8B using utf8mb4)
);

SET @aid = LAST_INSERT_ID();

INSERT INTO yc_activity_config (
  activity_id, mp_show, home_banner, hot_show, show_countdown, countdown_style,
  show_register_count, hotel_need_register, live_need_register, register_show_live,
  register_show_hotel, hotel_once, cancel_register_cancel_hotel, login_sms,
  register_force_mobile, grid_template, qr_url, mobile_template, mobile_theme_color,
  mobile_background_url, mobile_blocks_json, mobile_notice, create_by, create_time
) VALUES (
  @aid, '1', '1', '1', '1', 'digital',
  '1', '0', '0', '0',
  '0', '0', '0', '0',
  '1', '68',
  CONCAT('http://localhost:9090/#/pages/meeting/home?activityId=', @aid),
  'standard', '#1E4BB5',
  '', '[]', convert(0xE6ACA2E8BF8EE58F82E58AA032303236E5B9B4E58DB1E9878DE79691E99ABEE79785E4BE8BE4B88EE9BABBE98689E5AE89E585A8E5ADA6E4B9A0E78FADE38082E8AFB7E68F90E5898DE5AE8CE68890E5ADA6E58886E6B3A8E5868CEFBC8CE5B9B6E79599E6848FE4BC9AE8AEAEE9809AE79FA5E4B88EE78EB0E59CBAE7ADBEE588B0E5AE89E68E92E38082 using utf8mb4), 'demo', NOW()
);

INSERT INTO yc_activity_grid (
  activity_id, title, icon_type, icon_key, icon_url, link_type, module_key,
  external_url, content, sort_order, status, del_flag, create_by, create_time
) VALUES
(@aid, convert(0xE4BC9AE8AEAEE9809AE79FA5 using utf8mb4), 'image', '', '/reference/meeting-38313/grid-notice.png', 'content', '', '', convert(0xE38090E4BC9AE8AEAEE9809AE79FA5E380910AE4BC9AE8AEAEE5908DE7A7B0EFBC9A32303236E5B9B4E58DB1E9878DE79691E99ABEE79785E4BE8BE4B88EE9BABBE98689E5AE89E585A8E5ADA6E4B9A0E78FAD0AE697B6E997B4EFBC9A32303236E5B9B43038E69C883037E697A52D3038E697A50AE59CB0E782B9EFBC9AE6B389E5B79EE6B5B7E4B89DE58D9AE4BA9AE59BBDE99985E98592E5BA970AE4B8BBE58A9EE58D95E4BD8DEFBC9AE7A68FE5BBBAE79C81E7B2BEE58786E58CBBE5ADA6E7A791E68A80E58D8FE4BC9AE38081E7A68FE5BBBAE58CBBE7A791E5A4A7E5ADA6E99984E5B19EE7ACACE4BA8CE58CBBE999A20AE8AFB7E58F82E4BC9AE4BBA3E8A1A8E68F90E5898DE5AE8CE68890E6B3A8E5868CEFBC8CE68C89E9809AE79FA5E697B6E997B4E68AA5E588B0E38082 using utf8mb4), 1, '1', '0', 'demo', NOW()),
(@aid, convert(0xE5A4A7E4BC9AE8AEAEE7A88B using utf8mb4), 'image', '', '/reference/meeting-38313/grid-agenda.png', 'module', 'schedule', '', '', 2, '1', '0', 'demo', NOW()),
(@aid, convert(0xE5ADA6E58886E6B3A8E5868C using utf8mb4), 'image', '', '/reference/meeting-38313/grid-apply.png', 'module', 'apply', '', '', 3, '1', '0', 'demo', NOW()),
(@aid, convert(0xE5ADA6E58886E9A1BBE79FA5 using utf8mb4), 'image', '', '/reference/meeting-38313/grid-credit.png', 'content', '', '', convert(0xE38090E5ADA6E58886E9A1BBE79FA5E380910A312E20E8AFB7E68C89E697B6E7ADBEE588B0E38081E7ADBEE98080EFBC8CE585A8E7A88BE58F82E58AA0E5ADA6E4B9A0E380820A322E20E5ADA6E58886E68E88E4BA88E4BBA5E5AE9EE99985E588B0E4BC9AE58F8AE88083E6A0B8E68385E586B5E4B8BAE58786E380820A332E20E5A682E69C89E79691E997AEE8AFB7E9809AE8BF87E2809CE88194E7B3BBE68891E4BBACE2809DE592A8E8AFA2E4BC9AE58AA1E7BB84E38082 using utf8mb4), 4, '1', '0', 'demo', NOW()),
(@aid, convert(0xE4BC9AE8AEAEE59CB0E59D80 using utf8mb4), 'image', '', '/reference/meeting-38313/grid-address.png', 'module', 'venue', '', '', 5, '1', '0', 'demo', NOW()),
(@aid, convert(0xE88194E7B3BBE68891E4BBAC using utf8mb4), 'image', '', '/reference/meeting-38313/grid-contact.png', 'content', '', '', convert(0xE38090E88194E7B3BBE68891E4BBACE380910AE4BC9AE58AA1E592A8E8AFA2EFBC9A303539352D303030303030300AE4BC9AE58AA1E694AFE68C81EFBC9AE58EA6E997A8E4B8ADE5A4A9E4BC9AE58AA10AE5B7A5E4BD9CE697B6E997B4EFBC9AE5B7A5E4BD9CE697A52030393A30302D31383A3030 using utf8mb4), 6, '1', '0', 'demo', NOW());

INSERT INTO yc_grid_bottom (
  activity_id, bottom_name, bottom_type, link_url, module_key, phone, icon_url,
  sort_order, status, del_flag, create_by, create_time
) VALUES
(@aid, convert(0xE9A696E9A1B5 using utf8mb4), 'module', '', 'schedule', '', '/reference/meeting-38313/grid-agenda.png', 1, '1', '0', 'demo', NOW()),
(@aid, convert(0xE68AA5E5908D using utf8mb4), 'module', '', 'apply', '', '/reference/meeting-38313/grid-apply.png', 2, '1', '0', 'demo', NOW()),
(@aid, convert(0xE88194E7B3BB using utf8mb4), 'phone', '', '', '05950000000', '/reference/meeting-38313/grid-contact.png', 3, '1', '0', 'demo', NOW());

INSERT INTO yc_apply_channel (
  activity_id, channel_name, is_main, parent_id, sort_order, price_type, price,
  quota, need_invite, need_audit, need_invoice, visible, sms_notify, del_flag,
  create_by, create_time, remark
) VALUES (
  @aid, convert(0xE4B8AAE4BABAE58F82E4BC9AE6B3A8E5868C using utf8mb4), '1', 0, 1, 'free', 0, 0, '0', '0', '0', '1', '0', '0',
  'demo', NOW(), convert(0xE58F82E88083E7AB993338333133E5ADA6E58886E6B3A8E5868CE9809AE98193 using utf8mb4)
);
SET @cid = LAST_INSERT_ID();

INSERT INTO yc_apply_field (
  channel_id, activity_id, field_scope, field_key, field_name, field_type,
  placeholder, options_json, required_flag, enabled_flag, sort_order, del_flag,
  create_by, create_time
) VALUES
(@cid, @aid, 'standard', 'name', convert(0xE5A793E5908D using utf8mb4), 'input', convert(0xE8AFB7E8BE93E585A5E5A793E5908D using utf8mb4), '', '1', '1', 1, '0', 'demo', NOW()),
(@cid, @aid, 'standard', 'mobile', convert(0xE6898BE69CBAE58FB7 using utf8mb4), 'input', convert(0xE8AFB7E8BE93E585A5E6898BE69CBAE58FB7 using utf8mb4), '', '1', '1', 2, '0', 'demo', NOW()),
(@cid, @aid, 'standard', 'company', convert(0xE58D95E4BD8D using utf8mb4), 'input', convert(0xE8AFB7E8BE93E585A5E58D95E4BD8D using utf8mb4), '', '1', '1', 3, '0', 'demo', NOW()),
(@cid, @aid, 'standard', 'title', convert(0xE8818CE7A7B0 using utf8mb4), 'input', convert(0xE8AFB7E8BE93E585A5E8818CE7A7B0 using utf8mb4), '', '0', '1', 4, '0', 'demo', NOW());

INSERT INTO yc_venue (
  activity_id, venue_name, is_live, live_status, cover_url, sort_order, del_flag, create_by, create_time, remark
) VALUES (
  @aid, convert(0xE6B389E5B79EE6B5B7E4B89DE58D9AE4BA9AE59BBDE99985E98592E5BA97 using utf8mb4), '0', '0', '/reference/meeting-38313/cover-banner.jpg', 1, '0', 'demo', NOW(), convert(0xE7A68FE5BBBAC2B7E6B389E5B79E using utf8mb4)
);
SET @vid = LAST_INSERT_ID();

INSERT INTO yc_topic (
  activity_id, venue_id, topic_name, start_time, end_time, sort_order, del_flag, create_by, create_time, remark
) VALUES (
  @aid, @vid, convert(0xE4B8BBE4BC9AE59CBA using utf8mb4), '2026-08-07 08:30:00', '2026-08-08 12:00:00', 1, '0', 'demo', NOW(), ''
);
SET @tid = LAST_INSERT_ID();

INSERT INTO yc_schedule (
  activity_id, topic_id, schedule_name, schedule_date, start_time, end_time, speaker_names, sort_order, del_flag, create_by, create_time, remark
) VALUES
(@aid, @tid, convert(0xE5BC80E5B995E5BC8FE4B88EE4B893E9A298E68E88E8AFBE using utf8mb4), '2026-08-07', '2026-08-07 08:30:00', '2026-08-07 12:00:00', convert(0xE4B893E5AEB6E59BA2E9989F using utf8mb4), 1, '0', 'demo', NOW(), ''),
(@aid, @tid, convert(0xE58DB1E9878DE79691E99ABEE79785E4BE8BE8AEA8E8AEBA using utf8mb4), '2026-08-07', '2026-08-07 14:00:00', '2026-08-07 17:30:00', convert(0xE4B893E5AEB6E59BA2E9989F using utf8mb4), 2, '0', 'demo', NOW(), ''),
(@aid, @tid, convert(0xE9BABBE98689E5AE89E585A8E4B893E9A298E4B88EE7BB93E4B89A using utf8mb4), '2026-08-08', '2026-08-08 08:30:00', '2026-08-08 12:00:00', convert(0xE4B893E5AEB6E59BA2E9989F using utf8mb4), 3, '0', 'demo', NOW(), '');

SELECT @aid AS activity_id, @code AS activity_code;
