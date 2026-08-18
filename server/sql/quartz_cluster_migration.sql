-- Additive migration for blue-green Quartz clustering.
-- This file intentionally contains no DROP statements.

CREATE TABLE IF NOT EXISTS QRTZ_JOB_DETAILS (
    sched_name varchar(120) NOT NULL,
    job_name varchar(200) NOT NULL,
    job_group varchar(200) NOT NULL,
    description varchar(250) NULL,
    job_class_name varchar(250) NOT NULL,
    is_durable varchar(1) NOT NULL,
    is_nonconcurrent varchar(1) NOT NULL,
    is_update_data varchar(1) NOT NULL,
    requests_recovery varchar(1) NOT NULL,
    job_data blob NULL,
    PRIMARY KEY (sched_name, job_name, job_group)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_TRIGGERS (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    job_name varchar(200) NOT NULL,
    job_group varchar(200) NOT NULL,
    description varchar(250) NULL,
    next_fire_time bigint(13) NULL,
    prev_fire_time bigint(13) NULL,
    priority integer NULL,
    trigger_state varchar(16) NOT NULL,
    trigger_type varchar(8) NOT NULL,
    start_time bigint(13) NOT NULL,
    end_time bigint(13) NULL,
    calendar_name varchar(200) NULL,
    misfire_instr smallint(2) NULL,
    job_data blob NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_triggers_job FOREIGN KEY (sched_name, job_name, job_group)
        REFERENCES QRTZ_JOB_DETAILS (sched_name, job_name, job_group)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_SIMPLE_TRIGGERS (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    repeat_count bigint(7) NOT NULL,
    repeat_interval bigint(12) NOT NULL,
    times_triggered bigint(10) NOT NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_simple_trig FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES QRTZ_TRIGGERS (sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_CRON_TRIGGERS (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    cron_expression varchar(200) NOT NULL,
    time_zone_id varchar(80) NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_cron_trig FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES QRTZ_TRIGGERS (sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_BLOB_TRIGGERS (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    blob_data blob NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_blob_trig FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES QRTZ_TRIGGERS (sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_CALENDARS (
    sched_name varchar(120) NOT NULL,
    calendar_name varchar(200) NOT NULL,
    calendar blob NOT NULL,
    PRIMARY KEY (sched_name, calendar_name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_PAUSED_TRIGGER_GRPS (
    sched_name varchar(120) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    PRIMARY KEY (sched_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_FIRED_TRIGGERS (
    sched_name varchar(120) NOT NULL,
    entry_id varchar(95) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    instance_name varchar(200) NOT NULL,
    fired_time bigint(13) NOT NULL,
    sched_time bigint(13) NOT NULL,
    priority integer NOT NULL,
    state varchar(16) NOT NULL,
    job_name varchar(200) NULL,
    job_group varchar(200) NULL,
    is_nonconcurrent varchar(1) NULL,
    requests_recovery varchar(1) NULL,
    PRIMARY KEY (sched_name, entry_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_SCHEDULER_STATE (
    sched_name varchar(120) NOT NULL,
    instance_name varchar(200) NOT NULL,
    last_checkin_time bigint(13) NOT NULL,
    checkin_interval bigint(13) NOT NULL,
    PRIMARY KEY (sched_name, instance_name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_LOCKS (
    sched_name varchar(120) NOT NULL,
    lock_name varchar(40) NOT NULL,
    PRIMARY KEY (sched_name, lock_name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS QRTZ_SIMPROP_TRIGGERS (
    sched_name varchar(120) NOT NULL,
    trigger_name varchar(200) NOT NULL,
    trigger_group varchar(200) NOT NULL,
    str_prop_1 varchar(512) NULL,
    str_prop_2 varchar(512) NULL,
    str_prop_3 varchar(512) NULL,
    int_prop_1 int NULL,
    int_prop_2 int NULL,
    long_prop_1 bigint NULL,
    long_prop_2 bigint NULL,
    dec_prop_1 numeric(13,4) NULL,
    dec_prop_2 numeric(13,4) NULL,
    bool_prop_1 varchar(1) NULL,
    bool_prop_2 varchar(1) NULL,
    PRIMARY KEY (sched_name, trigger_name, trigger_group),
    CONSTRAINT fk_qrtz_simprop_trig FOREIGN KEY (sched_name, trigger_name, trigger_group)
        REFERENCES QRTZ_TRIGGERS (sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB;
