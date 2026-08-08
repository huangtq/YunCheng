-- Apply order for existing databases

create table if not exists yc_apply_order (
  order_id          bigint(20)      not null auto_increment    comment '订单ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  channel_id        bigint(20)      default null               comment '报名通道ID',
  order_no          varchar(64)     not null                   comment '订单号',
  contact_name      varchar(64)     not null                   comment '参会人姓名',
  mobile            varchar(20)     not null                   comment '手机号',
  gender            varchar(10)     default ''                 comment '性别',
  company           varchar(200)    default ''                 comment '单位',
  order_status      char(1)         default '0'                comment '0已报名 2已取消',
  checkin_status    char(1)         default '0'                comment '0未签到 1已签到',
  checkin_time      datetime                                   comment '签到时间',
  form_json         text                                       comment '扩展表单JSON',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (order_id),
  unique key uk_apply_order_no (order_no),
  key idx_apply_order_activity (activity_id),
  key idx_apply_order_channel (channel_id),
  key idx_apply_order_mobile (mobile)
) engine=innodb auto_increment=1 comment = '会议报名订单';

insert ignore into sys_menu values('2122', '报名订单查询', '2101', '22', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:order:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2123', '报名订单新增', '2101', '23', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:order:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2124', '报名订单修改', '2101', '24', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:order:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2125', '报名订单删除', '2101', '25', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:order:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_role_menu values ('1', '2122');
insert ignore into sys_role_menu values ('1', '2123');
insert ignore into sys_role_menu values ('1', '2124');
insert ignore into sys_role_menu values ('1', '2125');