-- Add content-page storage for existing grid tables.
-- Run this migration when the initialization SQL was already applied.
alter table yc_activity_grid
    add column content text null comment 'grid content page body' after external_url;
