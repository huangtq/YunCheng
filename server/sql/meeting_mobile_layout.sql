-- Mobile meeting layout fields
alter table yc_activity_config
  add column mobile_template varchar(32) default 'standard' comment 'mobile layout template',
  add column mobile_theme_color varchar(32) default '#1f6feb' comment 'mobile theme color',
  add column mobile_background_url varchar(500) default '' comment 'mobile custom background',
  add column mobile_blocks_json text comment 'mobile blocks JSON',
  add column mobile_notice text comment 'mobile entry notice';
