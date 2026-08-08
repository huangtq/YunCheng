-- Navigation / Grid Bottom for existing databases

create table if not exists yc_activity_nav (
  nav_id            bigint(20)      not null auto_increment    comment '导航ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  title             varchar(100)    not null                   comment '标题',
  address           varchar(300)    default ''                 comment '地址',
  longitude         varchar(30)     default ''                 comment '经度',
  latitude          varchar(30)     default ''                 comment '纬度',
  phone             varchar(50)     default ''                 comment '电话',
  cover_url         varchar(500)    default ''                 comment '封面',
  sort_order        int(11)         default 0                  comment '排序',
  status            char(1)         default '1'                comment '0停用 1启用',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (nav_id),
  key idx_nav_activity (activity_id)
) engine=innodb auto_increment=1 comment='会议导航';

create table if not exists yc_grid_bottom (
  bottom_id         bigint(20)      not null auto_increment    comment '底部项ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  bottom_name       varchar(100)    not null                   comment '名称',
  bottom_type       varchar(20)     default 'link'             comment 'link/module/phone/text',
  link_url          varchar(500)    default ''                 comment '链接',
  module_key        varchar(50)     default ''                 comment '模块key',
  phone             varchar(50)     default ''                 comment '电话',
  icon_url          varchar(500)    default ''                 comment '图标',
  sort_order        int(11)         default 0                  comment '排序',
  status            char(1)         default '1'                comment '0停用 1启用',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (bottom_id),
  key idx_bottom_activity (activity_id)
) engine=innodb auto_increment=1 comment='九宫格底部配置';

insert ignore into sys_menu values('2196', '导航查询', '2101', '96', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:nav:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2197', '导航新增', '2101', '97', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:nav:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2198', '导航修改', '2101', '98', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:nav:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2199', '导航删除', '2101', '99', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:nav:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2200', '底部查询', '2101', '100', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:bottom:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2201', '底部新增', '2101', '101', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:bottom:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2202', '底部修改', '2101', '102', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:bottom:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2203', '底部删除', '2101', '103', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:bottom:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_role_menu values ('1','2196'),('1','2197'),('1','2198'),('1','2199'),('1','2200'),('1','2201'),('1','2202'),('1','2203');