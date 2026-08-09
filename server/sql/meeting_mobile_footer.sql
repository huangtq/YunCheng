-- Configurable mobile meeting footer.

alter table yc_activity_config
    add column footer_enabled char(1) default '0' comment 'mobile footer enabled flag' after audio_loop,
    add column footer_text varchar(100) null comment 'mobile footer support text' after footer_enabled,
    add column footer_company varchar(100) null comment 'mobile footer company name' after footer_text,
    add column footer_logo_url varchar(500) null comment 'mobile footer logo URL' after footer_company,
    add column footer_link_url varchar(500) null comment 'mobile footer link URL' after footer_logo_url;
