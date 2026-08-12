-- Phase 1: versioned mobile home pages and reusable meeting content.
-- This migration is additive. Existing yc_activity_grid and activity config data remain untouched.

create table if not exists yc_activity_home_version (
  version_id        bigint(20)      not null auto_increment    comment '首页版本ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  version_no        int(11)         not null                   comment '活动内版本号',
  status            varchar(20)     not null default 'draft'   comment 'draft/published/archived',
  schema_version    varchar(20)     not null default '1'       comment '页面JSON schema版本',
  page_json         longtext        not null                   comment '页面编排JSON',
  publish_remark    varchar(500)    default ''                comment '发布备注',
  published_by      varchar(64)     default ''                comment '发布人',
  published_time    datetime                                   comment '发布时间',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (version_id),
  unique key uk_home_version_no (activity_id, version_no),
  key idx_home_version_active (activity_id, status, del_flag)
) engine=innodb auto_increment=1 comment='会议移动端首页版本';

create table if not exists yc_meeting_content (
  content_id        bigint(20)      not null auto_increment    comment '内容ID',
  activity_id       bigint(20)      not null                   comment '会议ID',
  title             varchar(200)    not null                   comment '标题',
  summary           varchar(1000)   default ''                comment '摘要',
  content_html      longtext                                   comment '清洗后的富文本',
  cover_url         varchar(500)    default ''                comment '封面图',
  visibility        varchar(20)     not null default 'public'  comment 'public/login/registered',
  status            varchar(20)     not null default 'draft'   comment 'draft/published/archived',
  valid_start       datetime                                   comment '生效开始',
  valid_end         datetime                                   comment '生效结束',
  sort_order        int(11)         default 0                 comment '排序',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (content_id),
  key idx_meeting_content_activity (activity_id, status, visibility, del_flag)
) engine=innodb auto_increment=1 comment='会议通用内容页';

create table if not exists yc_meeting_content_attachment (
  attachment_id     bigint(20)      not null auto_increment    comment '附件ID',
  content_id        bigint(20)      not null                   comment '内容ID',
  file_name         varchar(255)    not null                   comment '显示文件名',
  file_url          varchar(1000)   not null                   comment '受控文件地址',
  file_type         varchar(50)     default ''                comment '文件类型',
  file_size         bigint(20)      default 0                 comment '文件大小字节',
  visibility        varchar(20)     not null default 'public'  comment 'public/login/registered',
  valid_start       datetime                                   comment '生效开始',
  valid_end         datetime                                   comment '生效结束',
  sort_order        int(11)         default 0                 comment '排序',
  status            char(1)         default '1'                comment '0停用 1启用',
  del_flag          char(1)         default '0'                comment '删除标志',
  create_by         varchar(64)     default ''                comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (attachment_id),
  key idx_content_attachment (content_id, status, del_flag)
) engine=innodb auto_increment=1 comment='会议内容附件';

-- P0 administration permissions. INSERT IGNORE keeps this additive script re-runnable.
insert ignore into sys_menu values('2122', '会议首页查询', '2101', '22', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:home:list', '#', 'admin', sysdate(), '', null, '查看首页版本与草稿');
insert ignore into sys_menu values('2123', '会议首页编辑', '2101', '23', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:home:edit', '#', 'admin', sysdate(), '', null, '保存首页草稿');
insert ignore into sys_menu values('2124', '会议首页发布', '2101', '24', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:home:publish', '#', 'admin', sysdate(), '', null, '发布或回滚首页版本');
insert ignore into sys_menu values('2125', '会议内容查询', '2101', '25', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:content:list', '#', 'admin', sysdate(), '', null, '查看会议内容');
insert ignore into sys_menu values('2126', '会议内容新增', '2101', '26', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:content:add', '#', 'admin', sysdate(), '', null, '新增会议内容');
insert ignore into sys_menu values('2127', '会议内容编辑', '2101', '27', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:content:edit', '#', 'admin', sysdate(), '', null, '编辑会议内容及附件');
insert ignore into sys_menu values('2128', '会议内容删除', '2101', '28', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:content:remove', '#', 'admin', sysdate(), '', null, '删除会议内容及附件');
