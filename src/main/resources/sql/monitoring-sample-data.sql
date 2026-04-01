insert into tb_mon_task_m (
    task_id,
    task_nm,
    task_type_cd,
    task_cntnt,
    exec_param,
    success_param,
    schedule_val,
    task_prio,
    active_yn,
    fst_reg_dtm,
    fnl_upt_dtm
) values (
    1,
    'Local Health Check',
    'URL_HEALTH_CHECK',
    '로컬 애플리케이션 health 체크',
    '{"url":"http://localhost:8080/actuator/health","method":"GET","timeoutMs":3000,"headers":{"Accept":"application/json"}}'::jsonb,
    '{"expectedHttpStatusCode":200,"expectedStatus":"UP"}'::jsonb,
    '0 */1 * * * *',
    10,
    'Y',
    now(),
    now()
);

insert into tb_mon_task_m (
    task_id,
    task_nm,
    task_type_cd,
    task_cntnt,
    exec_param,
    success_param,
    schedule_val,
    task_prio,
    active_yn,
    fst_reg_dtm,
    fnl_upt_dtm
) values (
    2,
    'Module A DB Query Check',
    'DB_QUERY_CHECK',
    'module-a 기본 연결 확인',
    '{"db":"module-a","query":"SELECT 1"}'::jsonb,
    '{"comparison":"EQUALS","expectedValue":1}'::jsonb,
    '0 */1 * * * *',
    20,
    'Y',
    now(),
    now()
);