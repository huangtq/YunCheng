-- Rich mobile meeting layout migration.
-- Keeps existing grid rows compatible while adding tile geometry and media content.

alter table yc_activity_config
    add column audio_url varchar(500) null comment 'mobile meeting background audio URL' after mobile_notice,
    add column audio_autoplay char(1) default '0' comment 'mobile audio autoplay flag' after audio_url,
    add column audio_loop char(1) default '1' comment 'mobile audio loop flag' after audio_autoplay;

alter table yc_activity_grid
    add column content_type varchar(20) default 'text' comment 'content page type: text/image/url' after content,
    add column content_url varchar(500) null comment 'content page media URL' after content_type,
    add column tile_row int default 0 comment 'tile grid row, zero means automatic' after sort_order,
    add column tile_col int default 0 comment 'tile grid column, zero means automatic' after tile_row,
    add column tile_row_span int default 1 comment 'tile row span' after tile_col,
    add column tile_col_span int default 1 comment 'tile column span' after tile_row_span;
