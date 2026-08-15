-- Remove the RuoYi external website menu from an existing database.
DELETE FROM sys_role_menu WHERE menu_id = 4;
DELETE FROM sys_menu WHERE menu_id = 4;
