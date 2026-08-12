-- P1: user meal-coupon instances and auditable redemption.
create table if not exists yc_user_meal_coupon (
  coupon_id bigint(20) not null auto_increment, activity_id bigint(20) not null, ticket_id bigint(20) not null,
  apply_order_id bigint(20) not null, coupon_status varchar(20) not null default 'available' comment 'available/redeemed/revoked/expired',
  valid_start datetime null, valid_end datetime null, issued_by varchar(64) default '', issued_time datetime not null,
  del_flag char(1) default '0', remark varchar(500), primary key(coupon_id), unique key uk_coupon_ticket_order(ticket_id,apply_order_id), key idx_coupon_user(activity_id,apply_order_id,coupon_status)
) engine=innodb comment='用户餐券实例';
create table if not exists yc_meal_redemption_log (
  redemption_id bigint(20) not null auto_increment, activity_id bigint(20) not null, coupon_id bigint(20) not null,
  ticket_id bigint(20) not null, apply_order_id bigint(20) not null, action varchar(20) not null default 'redeem' comment 'redeem/revoke',
  checkpoint varchar(100) default '', operator_name varchar(64) default '', device_id varchar(100) default '', occurred_time datetime not null,
  idempotency_key varchar(100) default '', result varchar(20) not null, reason varchar(500) default '', primary key(redemption_id), unique key uk_redemption_idempotency(idempotency_key), key idx_redemption_coupon(coupon_id,occurred_time)
) engine=innodb comment='餐券核销审计流水';
insert ignore into sys_menu values('2133', '餐券发放', '2101', '33', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:meal:issue', '#', 'admin', sysdate(), '', null, '按报名人发放或补发餐券');
insert ignore into sys_menu values('2134', '餐券核销', '2101', '34', '', '', '', '', 1, 0, 'F', '0', '0', 'meeting:meal:redeem', '#', 'admin', sysdate(), '', null, '核销用户餐券');
