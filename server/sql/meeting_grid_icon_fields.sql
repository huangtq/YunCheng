-- Add icon fields for existing meeting grid tables.
ALTER TABLE yc_activity_grid
    ADD COLUMN icon_type varchar(20) DEFAULT 'image' COMMENT 'icon type' AFTER title,
    ADD COLUMN icon_key varchar(100) DEFAULT '' COMMENT 'icon key' AFTER icon_type;
