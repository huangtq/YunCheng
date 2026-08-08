-- Apply form field configuration for existing databases.
-- Run this migration when the initialization SQL was already applied.

create table if not exists yc_apply_field (
  field_id          bigint(20)      not null auto_increment    comment '�ֶ�ID',
  channel_id        bigint(20)      not null                   comment 'ͨ��ID',
  activity_id       bigint(20)      not null                   comment '����ID',
  field_scope       varchar(20)     default 'standard'         comment 'standard��׼/extend��չ',
  field_key         varchar(64)     default ''                 comment '�ֶα�ʶ',
  field_name        varchar(100)    not null                   comment 'չʾ����',
  field_type        varchar(20)     default 'input'            comment 'input/radio/checkbox/upload/system/date/textarea/select',
  placeholder       varchar(200)    default ''                 comment '��ʾ�İ�',
  options_json      varchar(1000)   default ''                 comment 'ѡ�֧�� JSON �� �� / Ů',
  show_condition    varchar(500)    default null               comment '��ʾ����JSON',
  required_flag     char(1)         default '0'                comment '0ѡ�� 1����',
  enabled_flag      char(1)         default '0'                comment '0ͣ�� 1����',
  sort_order        int(11)         default 100                comment '����',
  del_flag          char(1)         default '0'                comment 'ɾ����־',
  create_by         varchar(64)     default ''                 comment '������',
  create_time       datetime                                   comment '����ʱ��',
  update_by         varchar(64)     default ''                 comment '������',
  update_time       datetime                                   comment '����ʱ��',
  remark            varchar(500)    default null               comment '��ע',
  primary key (field_id),
  key idx_apply_field_channel (channel_id),
  key idx_apply_field_activity (activity_id)
) engine=innodb auto_increment=1 comment = '���������ֶ�����';

insert ignore into sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
values (120, '�����ֶ�����', 'field_type', '0', 'admin', sysdate(), '���������ֶ�����');
insert ignore into sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time) values
(1201, 1, '�����ı�', 'input', 'field_type', 'default', 'N', '0', 'admin', sysdate()),
(1202, 2, '�����ı�', 'textarea', 'field_type', 'default', 'N', '0', 'admin', sysdate()),
(1203, 3, '��ѡ', 'radio', 'field_type', 'default', 'N', '0', 'admin', sysdate()),
(1204, 4, '��ѡ', 'checkbox', 'field_type', 'default', 'N', '0', 'admin', sysdate()),
(1205, 5, '����ѡ��', 'select', 'field_type', 'default', 'N', '0', 'admin', sysdate()),
(1206, 6, '����', 'date', 'field_type', 'default', 'N', '0', 'admin', sysdate()),
(1207, 7, '�ϴ�', 'upload', 'field_type', 'default', 'N', '0', 'admin', sysdate()),
(1208, 8, 'ϵͳ���', 'system', 'field_type', 'default', 'N', '0', 'admin', sysdate());

insert ignore into sys_menu values('2118', '�����ֶβ�ѯ', '2101', '18', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:apply:field:list', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2119', '�����ֶ�����', '2101', '19', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:apply:field:add', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2120', '�����ֶ��޸�', '2101', '20', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:apply:field:edit', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_menu values('2121', '�����ֶ�ɾ��', '2101', '21', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:apply:field:remove', '#', 'admin', sysdate(), '', null, '');
insert ignore into sys_role_menu values ('1', '2118');
insert ignore into sys_role_menu values ('1', '2119');
insert ignore into sys_role_menu values ('1', '2120');
insert ignore into sys_role_menu values ('1', '2121');