-- Add show_condition for existing yc_apply_field tables.
-- Safe to skip if column already exists.

ALTER TABLE yc_apply_field
  ADD COLUMN show_condition varchar(500) DEFAULT NULL COMMENT '显示条件JSON' AFTER options_json;