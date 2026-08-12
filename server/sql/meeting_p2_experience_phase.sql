-- P2: scheduled publication and portal interaction primitives.
alter table yc_activity_home_version add column if not exists publish_at datetime null comment '定时发布时间';
alter table yc_activity_home_version add column if not exists publish_mode varchar(20) not null default 'manual' comment 'manual/scheduled';
create table if not exists yc_meeting_entry_event (
  event_id bigint(20) not null auto_increment, activity_id bigint(20) not null, entry_id varchar(100) not null, event_type varchar(30) not null comment 'view/click/download/favorite', mp_user_id bigint(20) null, occurred_time datetime not null, context_json text, primary key(event_id), key idx_entry_event(activity_id,entry_id,event_type,occurred_time)
) engine=innodb comment='会议入口行为事件';

create table if not exists yc_meeting_feedback (
  feedback_id bigint(20) not null auto_increment,
  activity_id bigint(20) not null,
  mp_user_id bigint(20) null,
  rating tinyint null,
  content varchar(1000) default '',
  status varchar(20) not null default 'submitted' comment 'submitted/handled/closed',
  created_time datetime not null,
  primary key(feedback_id), key idx_feedback_activity(activity_id,created_time)
) engine=innodb comment='会议反馈';

insert ignore into sys_menu values('2135', '会议看板', '2101', '35', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:dashboard:view', '#', 'admin', sysdate(), '', null, '查看会议聚合运营指标');
