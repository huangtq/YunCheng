SET NAMES utf8mb4;
SET @code = CONVERT('REF-38569-TILE' USING utf8mb4) COLLATE utf8mb4_unicode_ci;
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
  @code, convert(0xE88DAFE789A92FE58CBBE79697E599A8E6A2B0E4B8B4E5BA8AE8AF95E9AA8CE8B4A8E9878FE7AEA1E79086E4B88EE4BCA6E79086E5AEA1E69FA5E5ADA6E4B9A0E59FB9E8AEADE78FAD using utf8mb4), '/reference/meeting-38569/hero.png',
  '2026-07-25 08:30:00', '2026-07-25 18:00:00',
  convert(0xE7A68FE5BBBAE79C81 using utf8mb4), convert(0xE58EA6E997A8E5B882 using utf8mb4), convert(0xE58EA6E997A8E5A4A7E5ADA6E99984E5B19EE5BF83E8A180E7AEA1E79785E58CBBE999A2 using utf8mb4), '',
  '1', '1', '1', 0, 0, 0, '0', 'demo', NOW(), convert(0xE68C89E58F82E88083E7AB993338353639E9858DE7BDAEE79A8454696C65E4B88DE8A784E58899E5AEABE6A0BCE4BC9AE8AEAE using utf8mb4)
);
SET @aid = LAST_INSERT_ID();

INSERT INTO yc_activity_config (
  activity_id, mp_show, home_banner, hot_show, show_countdown, countdown_style,
  show_register_count, hotel_need_register, live_need_register, register_show_live,
  register_show_hotel, hotel_once, cancel_register_cancel_hotel, login_sms,
  register_force_mobile, grid_template, qr_url, mobile_template, mobile_theme_color,
  mobile_background_url, mobile_blocks_json, mobile_notice,
  audio_url, audio_autoplay, audio_loop, create_by, create_time
) VALUES (
  @aid, '1', '1', '1', '1', 'digital', '0', '0', '0', '0',
  '0', '0', '0', '0', '0', 'tile',
  CONCAT('http://localhost:9090/#/pages/meeting/home?activityId=', @aid),
  'standard', '#061A74', '/reference/meeting-38569/background.jpg', '[]', '', '/reference/meeting-38569/audio.mp3', '0', '1',
  'demo', NOW()
);

INSERT INTO yc_activity_grid (
  activity_id, title, icon_type, icon_key, icon_url, link_type, module_key,
  external_url, content, content_type, content_url, sort_order,
  tile_row, tile_col, tile_row_span, tile_col_span, status, del_flag, create_by, create_time
) VALUES
(@aid, convert(0xE4BC9AE8AEAEE9809AE79FA5 using utf8mb4), 'image', '', '/reference/meeting-38569/tile-notice.png',
 'content', '', '', '', 'image', '/reference/meeting-38569/notice.jpg', 1, 1, 1, 1, 2, '1', '0', 'demo', NOW()),
(@aid, convert(0xE4BC9AE8AEAEE9A1BBE79FA5 using utf8mb4), 'image', '', '/reference/meeting-38569/tile-guide.png',
 'content', '', '', '', 'image', '/reference/meeting-38569/guide.jpg', 2, 1, 3, 1, 2, '1', '0', 'demo', NOW()),
(@aid, convert(0xE4BC9AE8AEAEE59CB0E59D80 using utf8mb4), 'image', '', '/reference/meeting-38569/tile-address.png',
 'url', '', 'https://www.amap.com/place/B0FFMBUJC3', '', 'text', '', 3, 1, 5, 1, 2, '1', '0', 'demo', NOW()),
(@aid, convert(0xE5A4A7E4BC9AE79BB4E692AD using utf8mb4), 'image', '', '/reference/meeting-38569/tile-live.png',
 'url', '', 'http://zhaoshengniuren.com/mp_yqh/#/lives/detail?detailId=2079451173535924225&id=2079451021689536514',
 '', 'text', '', 4, 2, 1, 2, 2, 3, '1', '0', 'demo', NOW()),
(@aid, convert(0xE5A4A7E4BC9AE8AEAEE7A88B using utf8mb4), 'image', '', '/reference/meeting-38569/tile-agenda.png',
 'content', '', '', '', 'image', '/reference/meeting-38569/agenda.jpg', 5, 2, 4, 1, 3, '1', '0', 'demo', NOW()),
(@aid, convert(0xE58F82E4BC9AE6B3A8E5868C using utf8mb4), 'image', '', '/reference/meeting-38569/tile-register.png',
 'module', 'apply', '', '', 'text', '', 6, 3, 4, 1, 3, '1', '0', 'demo', NOW()),
(@aid, convert(0xE5A4A7E4BC9AE59889E5AEBE using utf8mb4), 'image', '', '/reference/meeting-38569/tile-guest.png',
 'content', '', '', '', 'image', '/reference/meeting-38569/guest.jpg', 7, 4, 1, 1, 3, '1', '0', 'demo', NOW()),
(@aid, convert(0xE88194E7B3BBE68891E4BBAC using utf8mb4), 'image', '', '/reference/meeting-38569/tile-contact.png',
 'content', '', '', '', 'image', '/reference/meeting-38569/contact.jpg', 8, 4, 4, 1, 3, '1', '0', 'demo', NOW());

INSERT INTO yc_apply_channel (
  activity_id, channel_name, is_main, parent_id, sort_order, price_type, price,
  quota, deadline, need_invite, need_audit, need_invoice, visible, sms_notify,
  del_flag, create_by, create_time, remark
) VALUES (
  @aid, convert(0xE58F82E4BC9AE6B3A8E5868C using utf8mb4), '1', 0, 1, 'free', 0, 0, '2026-07-17 20:50:00',
  '0', '0', '0', '1', '0', '0', 'demo', NOW(), convert(0xE58F82E88083E7AB993338353639E68AA5E5908DE9809AE98193EFBC8CE5B7B2E68C89E58E9FE7AB99E688AAE6ADA2E697B6E997B4E9858DE7BDAE using utf8mb4)
);

SELECT @aid AS activity_id, @code AS activity_code;
