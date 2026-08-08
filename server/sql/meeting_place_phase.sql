-- Venue / Topic / Schedule for existing databases

create table if not exists yc_venue (
  venue_id          bigint(20)      not null auto_increment    comment '会场ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  venue_name        varchar(100)    not null                   comment '会场名称',
  is_live           char(1)         default '0'                comment '是否直播 0否 1是',
  cover_url         varchar(500)    default ''                 comment '封面/主视觉',
  live_start        datetime                                   comment '直播开始',
  live_end          datetime                                   comment '直播结束',
  live_status       char(1)         default '0'                comment '0预告 1直播中 2录播',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (venue_id),
  key idx_venue_activity (activity_id)
) engine=innodb auto_increment=1 comment='会议会场';

create table if not exists yc_topic (
  topic_id          bigint(20)      not null auto_increment    comment '主题ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  venue_id          bigint(20)      not null                   comment '会场ID',
  topic_name        varchar(200)    not null                   comment '主题名称',
  start_time        datetime                                   comment '开始时间',
  end_time          datetime                                   comment '结束时间',
  chair_names       varchar(500)    default ''                 comment '主席，逗号分隔',
  host_names        varchar(500)    default ''                 comment '主持人，逗号分隔',
  discuss_names     varchar(500)    default ''                 comment '讨论嘉宾，逗号分隔',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (topic_id),
  key idx_topic_activity (activity_id),
  key idx_topic_venue (venue_id)
) engine=innodb auto_increment=1 comment='会议主题';

create table if not exists yc_schedule (
  schedule_id       bigint(20)      not null auto_increment    comment '日程ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  topic_id          bigint(20)      not null                   comment '主题ID',
  schedule_name     varchar(200)    not null                   comment '日程名称',
  schedule_date     date                                       comment '会议日期',
  start_time        datetime                                   comment '开始时间',
  end_time          datetime                                   comment '结束时间',
  duration_min      int(11)         default 0                  comment '时长分钟',
  speaker_names     varchar(500)    default ''                 comment '讲者，逗号分隔',
  host_names        varchar(500)    default ''                 comment '主持，逗号分隔',
  discuss_names     varchar(500)    default ''                 comment '讨论，逗号分隔',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (schedule_id),
  key idx_schedule_activity (activity_id),
  key idx_schedule_topic (topic_id)
) engine=innodb auto_increment=1 comment='会议日程';

insert ignore into sys_menu values('2130', '会场查询', '2101', '30', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:venue:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2131', '会场新增', '2101', '31', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:venue:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2132', '会场修改', '2101', '32', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:venue:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2133', '会场删除', '2101', '33', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:venue:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2134', '主题查询', '2101', '34', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:topic:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2135', '主题新增', '2101', '35', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:topic:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2136', '主题修改', '2101', '36', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:topic:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2137', '主题删除', '2101', '37', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:topic:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2138', '日程查询', '2101', '38', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:schedule:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2139', '日程新增', '2101', '39', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:schedule:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2140', '日程修改', '2101', '40', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:schedule:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2141', '日程删除', '2101', '41', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:schedule:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_role_menu values ('1', '2130'),('1', '2131'),('1', '2132'),('1', '2133'),('1', '2134'),('1', '2135'),('1', '2136'),('1', '2137'),('1', '2138'),('1', '2139'),('1', '2140'),('1', '2141');