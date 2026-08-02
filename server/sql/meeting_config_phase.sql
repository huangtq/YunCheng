-- ----------------------------
-- 会议配置相关表
-- ----------------------------
drop table if exists yc_activity_config;
create table yc_activity_config (
  activity_id                bigint(20)      not null                   comment '会议ID',
  mp_show                    char(1)         default '1'                comment '公众号显示',
  home_banner                char(1)         default '0'                comment '首页轮播',
  hot_show                   char(1)         default '0'                comment '热门会议',
  show_countdown             char(1)         default '1'                comment '显示倒计时',
  countdown_style            varchar(32)     default 'classic'          comment '倒计时样式',
  show_register_count        char(1)         default '0'                comment '显示报名人数',
  hotel_need_register        char(1)         default '1'                comment '酒店需要报名',
  live_need_register         char(1)         default '1'                comment '直播需要报名',
  register_show_live         char(1)         default '1'                comment '报名成功显示直播',
  register_show_hotel        char(1)         default '1'                comment '报名成功显示酒店',
  hotel_once                 char(1)         default '1'                comment '只能订一次酒店',
  cancel_register_cancel_hotel char(1)       default '0'                comment '取消报名同步取消酒店',
  login_sms                  char(1)         default '0'                comment '登录需要短信验证码',
  register_force_mobile      char(1)         default '0'                comment '报名强制手机号登录',
  grid_template              varchar(64)     default 'grid3x3'          comment '九宫格模板',
  qr_url                     varchar(500)    default ''                 comment '二维码落地链接',
  create_by                  varchar(64)     default ''                 comment '创建者',
  create_time                datetime                                   comment '创建时间',
  update_by                  varchar(64)     default ''                 comment '更新者',
  update_time                datetime                                   comment '更新时间',
  remark                     varchar(500)    default null               comment '备注',
  primary key (activity_id)
) engine=innodb comment = '会议配置开关表';

drop table if exists yc_activity_grid;
create table yc_activity_grid (
  grid_id           bigint(20)      not null auto_increment    comment '九宫格项ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  title             varchar(100)    not null                   comment '标题',
  icon_url          varchar(500)    default ''                 comment '图标地址',
  link_type         varchar(20)     default 'none'             comment '链接类型 none/module/url',
  module_key        varchar(64)     default 'none'             comment '内置模块键',
  external_url      varchar(500)    default ''                 comment '外部链接',
  sort_order        int(11)         default 0                  comment '排序',
  status            char(1)         default '1'                comment '状态（0停用 1启用）',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (grid_id),
  key idx_grid_activity (activity_id)
) engine=innodb auto_increment=1 comment = '会议九宫格配置表';

drop table if exists yc_apply_channel;
create table yc_apply_channel (
  channel_id        bigint(20)      not null auto_increment    comment '通道ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  channel_name      varchar(100)    not null                   comment '通道名称',
  is_main           char(1)         default '0'                comment '是否主通道',
  parent_id         bigint(20)      default 0                  comment '上级通道',
  sort_order        int(11)         default 0                  comment '排序',
  price_type        varchar(20)     default 'free'             comment '价格类型 free/paid',
  price             decimal(10,2)   default 0.00               comment '价格',
  quota             int(11)         default 0                  comment '名额0不限',
  deadline          datetime                                   comment '截止时间',
  need_invite       char(1)         default '0'                comment '需要邀请码',
  need_audit        char(1)         default '0'                comment '需要审核',
  need_invoice      char(1)         default '0'                comment '需要发票',
  visible           char(1)         default '1'                comment '是否显示',
  sms_notify        char(1)         default '0'                comment '短信通知',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (channel_id),
  key idx_channel_activity (activity_id)
) engine=innodb auto_increment=1 comment = '会议报名通道表';

-- 权限按钮（挂在会议列表下）
insert into sys_menu values('2110', '九宫格查询', '2101', '10', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:grid:list', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2111', '九宫格新增', '2101', '11', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:grid:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2112', '九宫格修改', '2101', '12', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:grid:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2113', '九宫格删除', '2101', '13', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:grid:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2114', '报名通道查询', '2101', '14', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:apply:list', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2115', '报名通道新增', '2101', '15', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:apply:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2116', '报名通道修改', '2101', '16', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:apply:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2117', '报名通道删除', '2101', '17', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:apply:remove', '#', 'admin', sysdate(), '', null, '');
-- 授权给超级管理员角色
insert ignore into sys_role_menu values ('1', '2110');
insert ignore into sys_role_menu values ('1', '2111');
insert ignore into sys_role_menu values ('1', '2112');
insert ignore into sys_role_menu values ('1', '2113');
insert ignore into sys_role_menu values ('1', '2114');
insert ignore into sys_role_menu values ('1', '2115');
insert ignore into sys_role_menu values ('1', '2116');
insert ignore into sys_role_menu values ('1', '2117');
