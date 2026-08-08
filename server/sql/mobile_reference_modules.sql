-- Add module records for the three mobile reference demo meetings.
set @a3 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-STANDARD');
set @a4 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-IMAGE');
set @a5 = (select activity_id from yc_activity where activity_code = 'DEMO-MOBILE-FILES');

delete from yc_schedule where activity_id in (@a3, @a4, @a5);
delete from yc_topic where activity_id in (@a3, @a4, @a5);
delete from yc_guest where activity_id in (@a3, @a4, @a5);
delete from yc_activity_nav where activity_id in (@a3, @a4, @a5);
delete from yc_exhibitor where activity_id in (@a3, @a4, @a5);
delete from yc_meal_ticket where activity_id in (@a3, @a4, @a5);
delete from yc_hotel where activity_id in (@a3, @a4, @a5);

insert into yc_venue (activity_id, venue_name, is_live, sort_order, del_flag, create_by, create_time)
values (@a3, 'Main Venue', '0', 1, '0', 'demo', sysdate());
set @v3 = last_insert_id();
insert into yc_venue (activity_id, venue_name, is_live, sort_order, del_flag, create_by, create_time)
values (@a4, 'Image Map Venue', '0', 1, '0', 'demo', sysdate());
set @v4 = last_insert_id();
insert into yc_venue (activity_id, venue_name, is_live, sort_order, del_flag, create_by, create_time)
values (@a5, 'File Meeting Venue', '0', 1, '0', 'demo', sysdate());
set @v5 = last_insert_id();

insert into yc_topic (activity_id, venue_id, topic_name, start_time, end_time, sort_order, del_flag, create_by, create_time)
values (@a3, @v3, 'Opening and keynote', '2026-09-01 09:00:00', '2026-09-01 10:00:00', 1, '0', 'demo', sysdate());
set @t3 = last_insert_id();
insert into yc_topic (activity_id, venue_id, topic_name, start_time, end_time, sort_order, del_flag, create_by, create_time)
values (@a4, @v4, 'Image map conference agenda', '2026-09-10 09:00:00', '2026-09-10 10:00:00', 1, '0', 'demo', sysdate());
set @t4 = last_insert_id();
insert into yc_topic (activity_id, venue_id, topic_name, start_time, end_time, sort_order, del_flag, create_by, create_time)
values (@a5, @v5, 'File meeting keynote', '2026-09-20 09:00:00', '2026-09-20 10:00:00', 1, '0', 'demo', sysdate());
set @t5 = last_insert_id();

insert into yc_schedule (
  activity_id, topic_id, schedule_name, schedule_date, start_time, end_time,
  duration_min, speaker_names, sort_order, del_flag, create_by, create_time
) values
(@a3, @t3, 'Opening and keynote report', '2026-09-01', '2026-09-01 09:00:00', '2026-09-01 10:00:00', 60, 'Demo Speaker A', 1, '0', 'demo', sysdate()),
(@a4, @t4, 'Image map agenda report', '2026-09-10', '2026-09-10 09:00:00', '2026-09-10 10:00:00', 60, 'Demo Speaker B', 1, '0', 'demo', sysdate()),
(@a5, @t5, 'File meeting agenda report', '2026-09-20', '2026-09-20 09:00:00', '2026-09-20 10:00:00', 60, 'Demo Speaker C', 1, '0', 'demo', sysdate());

insert into yc_guest (
  activity_id, guest_name, org_name, title, intro, attend_flag, sort_order, del_flag, create_by, create_time
) values
(@a3, 'Demo Speaker A', 'Demo Medical Association', 'Chief Physician', 'Standard template guest', '1', 1, '0', 'demo', sysdate()),
(@a4, 'Demo Speaker B', 'Demo Conference Group', 'Conference Guest', 'Image map template guest', '1', 1, '0', 'demo', sysdate()),
(@a5, 'Demo Speaker C', 'Demo File Meeting Group', 'Conference Guest', 'File template guest', '1', 1, '0', 'demo', sysdate());

insert into yc_activity_nav (
  activity_id, title, address, longitude, latitude, status, del_flag, sort_order, create_by, create_time
) values
(@a3, 'Main venue navigation', 'Demo Main Venue', '118.5898', '24.9089', '1', '0', 1, 'demo', sysdate()),
(@a4, 'Image map venue navigation', 'Demo Image Map Venue', '118.0894', '24.4798', '1', '0', 1, 'demo', sysdate()),
(@a5, 'File meeting venue navigation', 'Demo File Meeting Venue', '119.2965', '26.0745', '1', '0', 1, 'demo', sysdate());

insert into yc_exhibitor (
  activity_id, exhibitor_name, intro, contact_name, booth_no, is_featured, sort_order, del_flag, create_by, create_time
) values
(@a3, 'Demo Exhibitor', 'Standard template exhibitor', 'Demo Contact', 'A-01', '1', 1, '0', 'demo', sysdate()),
(@a4, 'Image Map Exhibitor', 'Image map template exhibitor', 'Demo Contact', 'B-01', '1', 1, '0', 'demo', sysdate()),
(@a5, 'File Meeting Exhibitor', 'File template exhibitor', 'Demo Contact', 'C-01', '1', 1, '0', 'demo', sysdate());

insert into yc_meal_ticket (
  activity_id, ticket_name, meal_type, meal_date, total_quota, used_count, enabled, sort_order, del_flag, create_by, create_time
) values
(@a3, 'Demo lunch ticket', 'lunch', '2026-09-01', 100, 0, '1', 1, '0', 'demo', sysdate()),
(@a4, 'Image map lunch ticket', 'lunch', '2026-09-10', 100, 0, '1', 1, '0', 'demo', sysdate()),
(@a5, 'File meeting lunch ticket', 'lunch', '2026-09-20', 100, 0, '1', 1, '0', 'demo', sysdate());

insert into yc_hotel (
  activity_id, hotel_name, phone, address, sale_status, sort_order, del_flag, create_by, create_time
) values
(@a3, 'Demo Hotel', '0595-0000000', 'Demo Main Venue nearby', '1', 1, '0', 'demo', sysdate()),
(@a4, 'Image Map Hotel', '0592-0000000', 'Demo Image Map Venue nearby', '1', 1, '0', 'demo', sysdate()),
(@a5, 'File Meeting Hotel', '0591-0000000', 'Demo File Meeting Venue nearby', '1', 1, '0', 'demo', sysdate());
