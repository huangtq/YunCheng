-- Guest / Trip / TaskRule / Staff / Fee for existing databases

create table if not exists yc_guest (
  guest_id          bigint(20)      not null auto_increment    comment '嘉宾ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  guest_name        varchar(100)    not null                   comment '姓名',
  phone             varchar(20)     default ''                 comment '手机号',
  org_name          varchar(200)    default ''                 comment '工作单位',
  title             varchar(100)    default ''                 comment '职称',
  english_name      varchar(100)    default ''                 comment '英文名',
  guest_type        varchar(50)     default ''                 comment '专家类别',
  avatar            varchar(500)    default ''                 comment '头像',
  intro             varchar(2000)   default ''                 comment '简介',
  need_hotel        char(1)         default '0'                comment '是否需要酒店 0否 1是',
  check_in_date     date                                       comment '入住日期',
  check_out_date    date                                       comment '退房日期',
  id_card           varchar(30)     default ''                 comment '身份证号',
  attend_flag       char(1)         default '1'                comment '是否参会 0否 1是',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (guest_id),
  key idx_guest_activity (activity_id)
) engine=innodb auto_increment=1 comment='会议嘉宾';

create table if not exists yc_guest_trip (
  trip_id           bigint(20)      not null auto_increment    comment '行程ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  guest_id          bigint(20)      not null                   comment '嘉宾ID',
  trip_type         varchar(20)     default 'come'             comment 'come来程 return返程',
  transport_type    varchar(20)     default 'plane'            comment 'plane/train/car/other',
  transport_no      varchar(50)     default ''                 comment '班次',
  trip_date         date                                       comment '日期',
  from_place        varchar(100)    default ''                 comment '出发地',
  to_place          varchar(100)    default ''                 comment '到达地',
  depart_time       datetime                                   comment '出发时间',
  arrive_time       datetime                                   comment '到达时间',
  price             decimal(10,2)   default 0                  comment '价格',
  ticket_status     char(1)         default '0'                comment '0未出票 1已出票 2已取消',
  pickup_status     char(1)         default '0'                comment '0未确认 1已确认',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (trip_id),
  key idx_trip_activity (activity_id),
  key idx_trip_guest (guest_id)
) engine=innodb auto_increment=1 comment='嘉宾行程';

create table if not exists yc_guest_task_rule (
  rule_id           bigint(20)      not null auto_increment    comment '规则ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  rule_name         varchar(100)    not null                   comment '规则名称',
  role_type         varchar(20)     default 'speaker'          comment 'chair/host/speaker/discuss/custom',
  fee_amount        decimal(10,2)   default 0                  comment '默认劳务费',
  enabled           char(1)         default '1'                comment '是否启用',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (rule_id),
  key idx_rule_activity (activity_id)
) engine=innodb auto_increment=1 comment='任务规则';

create table if not exists yc_staff (
  staff_id          bigint(20)      not null auto_increment    comment '工作人员ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  staff_name        varchar(100)    not null                   comment '姓名',
  phone             varchar(20)     default ''                 comment '手机号',
  role_name         varchar(100)    default ''                 comment '岗位',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (staff_id),
  key idx_staff_activity (activity_id)
) engine=innodb auto_increment=1 comment='会议工作人员';

create table if not exists yc_guest_fee (
  fee_id            bigint(20)      not null auto_increment    comment '劳务费ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  guest_id          bigint(20)      not null                   comment '嘉宾ID',
  fee_amount        decimal(10,2)   default 0                  comment '劳务费金额',
  progress_status   char(1)         default '0'                comment '0待确认 1已确认 2已签字 3待打款',
  pay_status        char(1)         default '0'                comment '0未打款 1已打款 2失败',
  bank_name         varchar(100)    default ''                 comment '开户行',
  bank_account      varchar(50)     default ''                 comment '银行账号',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (fee_id),
  key idx_fee_activity (activity_id),
  key idx_fee_guest (guest_id)
) engine=innodb auto_increment=1 comment='嘉宾劳务费';

insert ignore into sys_menu values('2150', '嘉宾查询', '2101', '50', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:guest:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2151', '嘉宾新增', '2101', '51', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:guest:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2152', '嘉宾修改', '2101', '52', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:guest:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2153', '嘉宾删除', '2101', '53', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:guest:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2154', '行程查询', '2101', '54', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:trip:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2155', '行程新增', '2101', '55', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:trip:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2156', '行程修改', '2101', '56', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:trip:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2157', '行程删除', '2101', '57', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:trip:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2158', '规则查询', '2101', '58', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:rule:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2159', '规则新增', '2101', '59', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:rule:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2160', '规则修改', '2101', '60', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:rule:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2161', '规则删除', '2101', '61', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:rule:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2162', '人员查询', '2101', '62', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:staff:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2163', '人员新增', '2101', '63', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:staff:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2164', '人员修改', '2101', '64', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:staff:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2165', '人员删除', '2101', '65', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:staff:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2166', '劳务查询', '2101', '66', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:fee:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2167', '劳务新增', '2101', '67', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:fee:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2168', '劳务修改', '2101', '68', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:fee:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2169', '劳务删除', '2101', '69', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:fee:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_role_menu values ('1','2150'),('1','2151'),('1','2152'),('1','2153'),('1','2154'),('1','2155'),('1','2156'),('1','2157'),('1','2158'),('1','2159'),('1','2160'),('1','2161'),('1','2162'),('1','2163'),('1','2164'),('1','2165'),('1','2166'),('1','2167'),('1','2168'),('1','2169');