-- ����Ź���ͼ��������ͼ�����Ǩ��
-- ��ִ�й��ýű��Ļ��������ظ�ִ�С�
alter table yc_activity_grid
  add column icon_type varchar(20) default 'image' comment 'ͼ������ image/icon' after title,
  add column icon_key varchar(100) default '' comment 'ͼ�����' after icon_type;

update yc_activity_grid
set icon_type = 'image',
    icon_key = ''
where icon_type is null or icon_type = '';
