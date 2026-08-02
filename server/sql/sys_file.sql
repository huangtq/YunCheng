-- ----------------------------
-- File management table
-- ----------------------------
drop table if exists sys_file;
create table sys_file (
  file_id           bigint(20)      not null auto_increment    comment '文件ID',
  original_name     varchar(255)    default ''                 comment '原始文件名',
  file_name         varchar(500)    default ''                 comment '存储相对路径（含 /profile 前缀）',
  url               varchar(500)    default ''                 comment '访问地址',
  file_suffix       varchar(50)     default ''                 comment '文件后缀',
  file_size         bigint(20)      default 0                  comment '文件大小（字节）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (file_id)
) engine=innodb auto_increment=1 comment = '文件管理表';

-- ----------------------------
-- File management menus
-- ----------------------------
insert into sys_menu values('2000', '文件管理', '0', '0', 'file', 'file/index', '', '', 1, 0, 'C', '0', '0', 'system:file:list', 'upload', 'admin', sysdate(), '', null, '文件管理菜单');
insert into sys_menu values('2001', '文件上传', '2000', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'system:file:upload', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2002', '文件删除', '2000', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'system:file:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2003', '文件查询', '2000', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'system:file:query', '#', 'admin', sysdate(), '', null, '');