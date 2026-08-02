-- ----------------------------
-- 会议管理第一期
-- ----------------------------
drop table if exists yc_activity;
create table yc_activity (
  activity_id       bigint(20)      not null auto_increment    comment '会议ID',
  activity_code     varchar(64)     not null                   comment '会议编号',
  activity_name     varchar(200)    not null                   comment '会议名称',
  cover_url         varchar(500)    default ''                 comment '主视觉地址',
  start_time        datetime                                   comment '开始时间',
  end_time          datetime                                   comment '结束时间',
  province          varchar(50)     default ''                 comment '省份',
  city              varchar(50)     default ''                 comment '城市',
  address           varchar(255)    default ''                 comment '详细地址',
  third_party_url   varchar(500)    default ''                 comment '第三方链接',
  is_show           char(1)         default '1'                comment '是否展示（0否 1是）',
  is_hot            char(1)         default '0'                comment '是否热门（0否 1是）',
  is_home           char(1)         default '0'                comment '是否首页（0否 1是）',
  register_count    int(11)         default 0                  comment '报名人数',
  visit_count       int(11)         default 0                  comment '点击/访问',
  view_count        int(11)         default 0                  comment '流量/观看',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (activity_id),
  unique key uk_activity_code (activity_code)
) engine=innodb auto_increment=1 comment = '会议活动表';

-- 会议管理目录与列表
insert into sys_menu values('2100', '会议管理', '0', '0', 'meeting', null, '', '', 1, 0, 'M', '0', '0', '', 'date', 'admin', sysdate(), '', null, '会议管理目录');
insert into sys_menu values('2101', '会议列表', '2100', '1', 'activity', 'meeting/activity/index', '', '', 1, 0, 'C', '0', '0', 'meeting:activity:list', 'list', 'admin', sysdate(), '', null, '会议列表菜单');
insert into sys_menu values('2102', '会议查询', '2101', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:activity:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2103', '会议新增', '2101', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:activity:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2104', '会议修改', '2101', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:activity:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2105', '会议删除', '2101', '4', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:activity:remove', '#', 'admin', sysdate(), '', null, '');

-- 文件管理挂到会议管理下
update sys_menu set parent_id = 2100, order_num = 2, path = 'file' where menu_id = 2000;