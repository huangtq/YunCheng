-- 为文件增加会议归属。activity_id 为空的记录继续作为全局文件保留。
alter table sys_file
    add column activity_id bigint(20) default null comment '所属会议ID，为空表示全局文件' after file_id,
    add index idx_sys_file_activity_id (activity_id);

-- 已迁移的 meeting-38569 参考资源归属到对应会议。
update sys_file
set activity_id = 12
where activity_id is null
  and file_name like '/profile/upload/reference/meeting-38569/%';
