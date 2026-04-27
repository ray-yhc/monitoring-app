create table if not exists tb_mon_task_m (
    task_id bigint primary key,
    task_nm varchar(200) not null,
    task_type_cd varchar(50) not null,
    task_cntnt varchar(1000),
    exec_param jsonb not null,
    success_param jsonb not null,
    schedule_val varchar(100) not null,
    task_prio integer not null default 100,
    active_yn char(1) not null default 'Y',
    last_exec_dtm timestamp,
    last_exec_rslt varchar(20),
    last_exec_rslt_msg varchar(2000),
    fst_reg_dtm timestamp not null,
    fnl_upt_dtm timestamp not null,
    constraint ck_tb_mon_task_m_active_yn check (active_yn in ('Y', 'N'))
);

create table if not exists tb_mon_task_hist_l (
    task_hist_id bigint primary key,
    task_id bigint not null,
    exec_dtm timestamp not null,
    exec_trg_type_cd varchar(20) not null,
    exec_rslt varchar(20) not null,
    exec_rslt_msg varchar(2000),
    exec_rslt_data jsonb,
    alert_event_type varchar(30) not null,
    exec_dur_ms bigint not null,
    fst_reg_dtm timestamp not null,
    fnl_upt_dtm timestamp not null,
    constraint fk_tb_mon_task_hist_l_task_id foreign key (task_id)
        references tb_mon_task_m(task_id)
);

create table if not exists tb_mon_report_group_m (
    report_group_id bigint primary key,
    report_group_name varchar(200) not null,
    description varchar(1000),
    chat_room_id varchar(200) not null,
    send_yn char(1) not null default 'Y',
    fst_reg_dtm timestamp not null,
    fnl_upt_dtm timestamp not null,
    constraint ck_tb_mon_report_group_m_send_yn check (send_yn in ('Y', 'N'))
);

create table if not exists tb_mon_task_report_group_r (
    task_id bigint not null,
    report_group_id bigint not null,
    fst_reg_dtm timestamp not null,
    fnl_upt_dtm timestamp not null,
    constraint pk_tb_mon_task_report_group_r primary key (task_id, report_group_id),
    constraint fk_tb_mon_task_report_group_r_task_id foreign key (task_id)
        references tb_mon_task_m(task_id),
    constraint fk_tb_mon_task_report_group_r_report_group_id foreign key (report_group_id)
        references tb_mon_report_group_m(report_group_id)
);

create index if not exists idx_tb_mon_task_m_01
    on tb_mon_task_m(active_yn, schedule_val, task_prio);

create index if not exists idx_tb_mon_task_m_02
    on tb_mon_task_m(task_type_cd);

create index if not exists idx_tb_mon_task_m_03
    on tb_mon_task_m(last_exec_rslt);

create index if not exists idx_tb_mon_task_hist_l_01
    on tb_mon_task_hist_l(task_id, exec_dtm desc);

create index if not exists idx_tb_mon_task_hist_l_02
    on tb_mon_task_hist_l(exec_dtm desc);

create index if not exists idx_tb_mon_task_hist_l_03
    on tb_mon_task_hist_l(alert_event_type, exec_dtm desc);

create index if not exists idx_tb_mon_report_group_m_01
    on tb_mon_report_group_m(send_yn);

create index if not exists idx_tb_mon_task_report_group_r_01
    on tb_mon_task_report_group_r(report_group_id, task_id);

create table if not exists tb_mon_report_l (
    report_id           bigint primary key,
    report_dt           date not null,
    analysis_start_dtm  timestamp not null,
    analysis_end_dtm    timestamp not null,
    total_exec_cnt      integer not null default 0,
    success_cnt         integer not null default 0,
    failure_cnt         integer not null default 0,
    error_cnt           integer not null default 0,
    exec_summary        text,
    ai_advice           text,
    report_status       varchar(20) not null default 'PENDING',
    error_msg           varchar(2000),
    fst_reg_dtm         timestamp not null,
    fnl_upt_dtm         timestamp not null
);

create index if not exists idx_tb_mon_report_l_01
    on tb_mon_report_l(report_dt desc);

create index if not exists idx_tb_mon_report_l_02
    on tb_mon_report_l(report_status, report_dt desc);