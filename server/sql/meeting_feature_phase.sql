-- Exhibitor / MealTicket for existing databases

create table if not exists yc_exhibitor (
  exhibitor_id      bigint(20)      not null auto_increment    comment '展商ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  exhibitor_name    varchar(200)    not null                   comment '商家名称',
  intro             varchar(2000)   default ''                 comment '简介',
  contact_name      varchar(100)    default ''                 comment '联系人',
  phone             varchar(20)     default ''                 comment '联系方式',
  logo_url          varchar(500)    default ''                 comment '商家图片',
  link_url          varchar(500)    default ''                 comment '第三方链接',
  booth_no          varchar(50)     default ''                 comment '展位号',
  is_featured       char(1)         default '0'                comment '是否大牌 0否 1是',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (exhibitor_id),
  key idx_exhibitor_activity (activity_id)
) engine=innodb auto_increment=1 comment='会议展商';

create table if not exists yc_meal_ticket (
  ticket_id         bigint(20)      not null auto_increment    comment '餐票ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  ticket_name       varchar(100)    not null                   comment '餐票名称',
  meal_type         varchar(20)     default 'lunch'            comment 'breakfast/lunch/dinner/tea',
  meal_date         date                                       comment '用餐日期',
  total_quota       int(11)         default 0                  comment '名额 0不限',
  used_count        int(11)         default 0                  comment '已核销数',
  enabled           char(1)         default '1'                comment '是否启用',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (ticket_id),
  key idx_meal_activity (activity_id)
) engine=innodb auto_increment=1 comment='会议餐票';

insert ignore into sys_menu values('2170', '展商查询', '2101', '70', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:exhibitor:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2171', '展商新增', '2101', '71', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:exhibitor:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2172', '展商修改', '2101', '72', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:exhibitor:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2173', '展商删除', '2101', '73', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:exhibitor:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2174', '餐票查询', '2101', '74', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:meal:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2175', '餐票新增', '2101', '75', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:meal:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2176', '餐票修改', '2101', '76', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:meal:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2177', '餐票删除', '2101', '77', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:meal:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_role_menu values ('1','2170'),('1','2171'),('1','2172'),('1','2173'),('1','2174'),('1','2175'),('1','2176'),('1','2177');