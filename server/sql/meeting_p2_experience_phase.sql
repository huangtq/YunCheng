-- P2: scheduled publication and portal interaction primitives.
-- Compatible with MySQL versions that do not support ADD COLUMN IF NOT EXISTS.
set @add_publish_at = (
  select if(count(*) = 0,
    'alter table yc_activity_home_version add column publish_at datetime null comment ''定时发布时间''',
    'select 1')
  from information_schema.columns
  where table_schema = database()
    and table_name = 'yc_activity_home_version'
    and column_name = 'publish_at'
);
prepare stmt_add_publish_at from @add_publish_at;
execute stmt_add_publish_at;
deallocate prepare stmt_add_publish_at;

set @add_publish_mode = (
  select if(count(*) = 0,
    'alter table yc_activity_home_version add column publish_mode varchar(20) not null default ''manual'' comment ''manual/scheduled''',
    'select 1')
  from information_schema.columns
  where table_schema = database()
    and table_name = 'yc_activity_home_version'
    and column_name = 'publish_mode'
);
prepare stmt_add_publish_mode from @add_publish_mode;
execute stmt_add_publish_mode;
deallocate prepare stmt_add_publish_mode;
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

-- 2135 is assigned to the existing meeting topic operation.
insert ignore into sys_menu values('2211', '会议看板', '2101', '111', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:dashboard:view', '#', 'admin', sysdate(), '', null, '查看会议聚合运营指标');
insert ignore into sys_role_menu values ('1', '2211');
