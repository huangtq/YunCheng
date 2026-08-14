-- 为文件增加会议归属。activity_id 为空的记录继续作为全局文件保留。
alter table sys_file
    add column activity_id bigint(20) default null comment '所属会议ID，为空表示全局文件' after file_id,
    add index idx_sys_file_activity_id (activity_id);
