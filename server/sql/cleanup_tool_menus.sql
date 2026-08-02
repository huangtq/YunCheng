-- 删除「系统工具」菜单及相关代码生成数据（适用于已初始化的库）
-- 菜单 ID：3, 115, 116, 117, 1055-1060

delete from sys_role_menu where menu_id in (3, 115, 116, 117, 1055, 1056, 1057, 1058, 1059, 1060);
delete from sys_menu where menu_id in (1055, 1056, 1057, 1058, 1059, 1060);
delete from sys_menu where menu_id in (115, 116, 117);
delete from sys_menu where menu_id = 3;

drop table if exists gen_table_column;
drop table if exists gen_table;
