-- Hotel / Room / Order / Assign for existing databases

create table if not exists yc_hotel (
  hotel_id          bigint(20)      not null auto_increment    comment '酒店ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  hotel_name        varchar(200)    not null                   comment '酒店名称',
  phone             varchar(50)     default ''                 comment '联系方式',
  address           varchar(300)    default ''                 comment '地址',
  cover_url         varchar(500)    default ''                 comment '封面图',
  sale_status       char(1)         default '1'                comment '0关闭售房 1开售',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (hotel_id),
  key idx_hotel_activity (activity_id)
) engine=innodb auto_increment=1 comment='会议酒店';

create table if not exists yc_hotel_room (
  room_id           bigint(20)      not null auto_increment    comment '房型ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  hotel_id          bigint(20)      not null                   comment '酒店ID',
  room_name         varchar(100)    not null                   comment '房型名称',
  bed_type          varchar(50)     default ''                 comment '床型',
  price             decimal(10,2)   default 0                  comment '单价',
  stock             int(11)         default 0                  comment '库存 0不限',
  reserved_stock    int(11)         default 0                  comment '已冻结库存',
  sort_order        int(11)         default 0                  comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (room_id),
  key idx_room_hotel (hotel_id),
  key idx_room_activity (activity_id)
) engine=innodb auto_increment=1 comment='酒店房型';

alter table yc_hotel_room add column if not exists reserved_stock int(11) default 0 comment '已冻结库存';

create table if not exists yc_hotel_order (
  order_id          bigint(20)      not null auto_increment    comment '订单ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  hotel_id          bigint(20)      not null                   comment '酒店ID',
  room_id           bigint(20)                                 comment '房型ID',
  guest_name        varchar(100)    not null                   comment '入住人',
  phone             varchar(20)     default ''                 comment '手机号',
  check_in_date     date                                       comment '入住日期',
  check_out_date    date                                       comment '退房日期',
  room_count        int(11)         default 1                  comment '间数',
  amount            decimal(10,2)   default 0                  comment '金额',
  order_status      char(1)         default '0'                comment '0待确认 1已确认 2已取消',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (order_id),
  key idx_horder_activity (activity_id),
  key idx_horder_hotel (hotel_id)
) engine=innodb auto_increment=1 comment='酒店订单';

create table if not exists yc_hotel_assign (
  assign_id         bigint(20)      not null auto_increment    comment '分房ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  hotel_id          bigint(20)      not null                   comment '酒店ID',
  order_id          bigint(20)                                 comment '关联订单',
  room_id           bigint(20)                                 comment '房型ID',
  guest_name        varchar(100)    not null                   comment '入住人',
  phone             varchar(20)     default ''                 comment '手机号',
  room_number       varchar(50)     default ''                 comment '房号',
  check_in_date     date                                       comment '入住日期',
  check_out_date    date                                       comment '退房日期',
  assign_status     char(1)         default '0'                comment '0未入住 1已入住 2已退房',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (assign_id),
  key idx_assign_activity (activity_id),
  key idx_assign_hotel (hotel_id)
) engine=innodb auto_increment=1 comment='酒店分房';

insert ignore into sys_menu values('2180', '酒店查询', '2101', '80', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:hotel:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2181', '酒店新增', '2101', '81', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:hotel:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2182', '酒店修改', '2101', '82', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:hotel:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2183', '酒店删除', '2101', '83', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:hotel:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2184', '房型查询', '2101', '84', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:room:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2185', '房型新增', '2101', '85', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:room:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2186', '房型修改', '2101', '86', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:room:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2187', '房型删除', '2101', '87', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:room:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2188', '酒店订单查询', '2101', '88', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:horder:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2189', '酒店订单新增', '2101', '89', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:horder:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2190', '酒店订单修改', '2101', '90', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:horder:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2191', '酒店订单删除', '2101', '91', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:horder:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2192', '分房查询', '2101', '92', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:assign:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2193', '分房新增', '2101', '93', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:assign:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2194', '分房修改', '2101', '94', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:assign:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2195', '分房删除', '2101', '95', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:assign:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_role_menu values
('1','2180'),('1','2181'),('1','2182'),('1','2183'),('1','2184'),('1','2185'),('1','2186'),('1','2187'),
('1','2188'),('1','2189'),('1','2190'),('1','2191'),('1','2192'),('1','2193'),('1','2194'),('1','2195');
