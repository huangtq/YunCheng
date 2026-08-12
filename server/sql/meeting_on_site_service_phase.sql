-- P1: attendee passes and auditable, idempotent check-in.
create table if not exists yc_attendee_pass (
  pass_id bigint(20) not null auto_increment comment '凭证ID',
  activity_id bigint(20) not null comment '会议ID',
  apply_order_id bigint(20) not null comment '报名订单ID',
  pass_status varchar(20) not null default 'active' comment 'active/revoked/expired',
  valid_start datetime null comment '生效开始',
  valid_end datetime null comment '生效结束',
  del_flag char(1) default '0', create_by varchar(64) default '', create_time datetime,
  update_by varchar(64) default '', update_time datetime, remark varchar(500),
  primary key (pass_id), unique key uk_pass_order (apply_order_id), key idx_pass_activity (activity_id, pass_status, del_flag)
) engine=innodb comment='会议参会电子凭证';

create table if not exists yc_checkin_log (
  checkin_log_id bigint(20) not null auto_increment comment '签到流水ID',
  activity_id bigint(20) not null, pass_id bigint(20) not null, apply_order_id bigint(20) not null,
  checkin_type varchar(30) not null default 'general' comment 'general/venue/meal',
  action varchar(20) not null default 'checkin' comment 'checkin/revoke',
  checkpoint varchar(100) default '' comment '核验点', operator_name varchar(64) default '' comment '操作人', device_id varchar(100) default '' comment '设备',
  occurred_time datetime not null comment '发生时间', idempotency_key varchar(100) default '' comment '幂等键',
  result varchar(20) not null default 'success' comment 'success/already_checked_in/rejected', reason varchar(500) default '',
  create_time datetime, primary key (checkin_log_id), unique key uk_checkin_idempotency (idempotency_key), key idx_checkin_pass (pass_id, checkin_type, occurred_time)
) engine=innodb comment='会议签到与核验审计流水';

insert ignore into sys_menu values('2129', '现场核验', '2101', '29', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:checkin:verify', '#', 'admin', sysdate(), '', null, '核验电子凭证并签到');
insert ignore into sys_menu values('2130', '签到撤销', '2101', '30', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:checkin:revoke', '#', 'admin', sysdate(), '', null, '撤销已签到记录');

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
