-- Remove legacy on-site verification permissions when this migration is reapplied.
delete from sys_menu where menu_id in ('2129', '2130') or perms in ('meeting:checkin:verify', 'meeting:checkin:revoke');

create table if not exists yc_meeting_notice (
  notice_id bigint(20) not null auto_increment, activity_id bigint(20) not null, title varchar(200) not null,
  content_html longtext, priority varchar(20) not null default 'normal' comment 'normal/important/urgent',
  visibility varchar(20) not null default 'public' comment 'public/login/registered', status varchar(20) not null default 'draft' comment 'draft/published/archived',
  publish_time datetime null, valid_end datetime null, del_flag char(1) default '0', create_by varchar(64) default '', create_time datetime, update_by varchar(64) default '', update_time datetime, remark varchar(500),
  primary key (notice_id), key idx_meeting_notice (activity_id,status,publish_time,del_flag)
) engine=innodb comment='会议通知';
create table if not exists yc_user_notice_read (
  read_id bigint(20) not null auto_increment, notice_id bigint(20) not null, mp_user_id bigint(20) not null, read_time datetime not null, primary key (read_id), unique key uk_notice_user (notice_id,mp_user_id)
) engine=innodb comment='会议通知已读记录';
insert ignore into sys_menu values('2131', '会议通知查询', '2101', '31', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:notice:list', '#', 'admin', sysdate(), '', null, '查看会议通知');
insert ignore into sys_menu values('2132', '会议通知编辑', '2101', '32', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:notice:edit', '#', 'admin', sysdate(), '', null, '编辑会议通知');
