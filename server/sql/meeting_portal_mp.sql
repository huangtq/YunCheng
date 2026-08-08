-- 微信服务号参会者
drop table if exists yc_mp_user;
create table yc_mp_user (
  user_id           bigint(20)      not null auto_increment    comment '参会者ID',
  openid            varchar(64)     not null                   comment '微信openid',
  unionid           varchar(64)     default null               comment '微信unionid',
  phone             varchar(20)     default ''                 comment '手机号',
  nickname          varchar(100)    default ''                 comment '昵称',
  avatar            varchar(500)    default ''                 comment '头像',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (user_id),
  unique key uk_yc_mp_openid (openid)
) engine=innodb comment = '微信参会者';