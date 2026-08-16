-- 九宫格内容页附件（增量迁移）
create table if not exists yc_activity_grid_attachment (
  attachment_id bigint(20) not null auto_increment comment '附件ID',
  activity_id bigint(20) not null comment '会议ID',
  grid_id bigint(20) not null comment '九宫格项ID',
  display_name varchar(255) not null comment '展示名称',
  download_name varchar(255) not null comment '下载名称',
  file_url varchar(1000) not null comment '文件地址',
  file_type varchar(50) default '' comment '文件类型',
  file_size bigint(20) default 0 comment '文件大小字节',
  sort_order int(11) default 0 comment '排序',
  status char(1) default '1' comment '0停用 1启用',
  del_flag char(1) default '0' comment '删除标志',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime comment '更新时间',
  primary key (attachment_id),
  key idx_grid_attachment (activity_id, grid_id, status, del_flag)
) engine=innodb auto_increment=1 comment='九宫格内容页附件';
