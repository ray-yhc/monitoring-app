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

create table if not exists tb_mon_task_hist_m (
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
    constraint fk_tb_mon_task_hist_m_task_id foreign key (task_id)
        references tb_mon_task_m(task_id)
);

create index if not exists idx_tb_mon_task_m_01
    on tb_mon_task_m(active_yn, schedule_val, task_prio);

create index if not exists idx_tb_mon_task_m_02
    on tb_mon_task_m(task_type_cd);

create index if not exists idx_tb_mon_task_m_03
    on tb_mon_task_m(last_exec_rslt);

create index if not exists idx_tb_mon_task_hist_m_01
    on tb_mon_task_hist_m(task_id, exec_dtm desc);

create index if not exists idx_tb_mon_task_hist_m_02
    on tb_mon_task_hist_m(exec_dtm desc);

create index if not exists idx_tb_mon_task_hist_m_03
    on tb_mon_task_hist_m(alert_event_type, exec_dtm desc);