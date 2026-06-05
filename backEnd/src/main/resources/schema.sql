-- =====================================================
-- 数据库设计 1.0 — 17 张表
-- =====================================================

-- 1. 患者表
CREATE TABLE IF NOT EXISTS patient (
    patient_id  VARCHAR(32)  PRIMARY KEY,
    name        VARCHAR(20)  NOT NULL,
    gender      SMALLINT,
    phone       VARCHAR(11)  NOT NULL UNIQUE,
    id_card     VARCHAR(18)  UNIQUE,
    address     VARCHAR(100),
    password    VARCHAR(64)  NOT NULL,
    birthday    DATE,
    create_time TIMESTAMP DEFAULT NOW(),
    update_time TIMESTAMP DEFAULT NOW(),
    is_deleted  SMALLINT DEFAULT 0
);

-- 2. 医生表
CREATE TABLE IF NOT EXISTS doctor (
    doctor_id     VARCHAR(32)  PRIMARY KEY,
    avatar        VARCHAR(255),
    name          VARCHAR(20)  NOT NULL,
    gender        SMALLINT,
    phone         VARCHAR(11)  NOT NULL UNIQUE,
    email         VARCHAR(50),
    position      VARCHAR(20),
    good_at       VARCHAR(200),
    introduction  TEXT,
    password      VARCHAR(64)  NOT NULL,
    department_id VARCHAR(32),
    status        SMALLINT DEFAULT 1,
    create_time   TIMESTAMP DEFAULT NOW(),
    is_deleted    SMALLINT DEFAULT 0
);

-- 3. 管理员表
CREATE TABLE IF NOT EXISTS admin (
    admin_id    VARCHAR(32)  PRIMARY KEY,
    name        VARCHAR(20)  NOT NULL,
    phone       VARCHAR(11)  NOT NULL UNIQUE,
    password    VARCHAR(64)  NOT NULL,
    create_time TIMESTAMP DEFAULT NOW()
);

-- 4. 科室表
CREATE TABLE IF NOT EXISTS department (
    dept_id       VARCHAR(32)  PRIMARY KEY,
    dept_name     VARCHAR(50)  NOT NULL,
    room_id       VARCHAR(32),
    max_capacity  INT,
    free_capacity INT,
    status        SMALLINT DEFAULT 1,
    create_time   TIMESTAMP DEFAULT NOW()
);

-- 5. 医生接诊时段表
CREATE TABLE IF NOT EXISTS doctor_schedule (
    schedule_id VARCHAR(32)  PRIMARY KEY,
    doctor_id   VARCHAR(32),
    doctor_name VARCHAR(20),
    time_json   JSON,
    max_num     INT,
    remain_num  INT,
    status      SMALLINT,
    price       DECIMAL(10,2),
    room        VARCHAR(50),
    create_time TIMESTAMP DEFAULT NOW()
);

-- 6. 挂号表
CREATE TABLE IF NOT EXISTS registration (
    register_id     VARCHAR(32)  PRIMARY KEY,
    patient_id      VARCHAR(32),
    doctor_id       VARCHAR(32),
    name            VARCHAR(20),
    gender          SMALLINT,
    birthday        DATE,
    chief_complaint VARCHAR(200),
    department      VARCHAR(50),
    consult_room    VARCHAR(50),
    visit_date      DATE,
    consult_time    VARCHAR(50),
    price           DECIMAL(10,2),
    pay_status      VARCHAR(20),
    consult_status  VARCHAR(30) DEFAULT 'PENDING',
    create_time     TIMESTAMP DEFAULT NOW()
);

-- 7. 病历表
CREATE TABLE IF NOT EXISTS medical_record (
    record_id    VARCHAR(32)  PRIMARY KEY,
    patient_id   VARCHAR(32),
    doctor_id    VARCHAR(32),
    register_id  VARCHAR(32),
    doctor_name  VARCHAR(20),
    patient_name VARCHAR(20),
    visit_age    INT,
    description  TEXT,
    visit_date   DATE,
    pay_status   VARCHAR(20),
    create_time  TIMESTAMP DEFAULT NOW()
);

-- 8. 检查报告表
CREATE TABLE IF NOT EXISTS check_report (
    report_id       VARCHAR(32)  PRIMARY KEY,
    patient_id      VARCHAR(32),
    register_id     VARCHAR(32),
    doctor_id       VARCHAR(32),
    patient_name    VARCHAR(20),
    gender          SMALLINT,
    age             INT,
    check_type      VARCHAR(20),
    check_item      VARCHAR(50),
    result          TEXT,
    reference_range TEXT,
    abnormal        VARCHAR(50),
    dept            VARCHAR(50),
    check_date      DATE,
    price           DECIMAL(10,2),
    pay_status      VARCHAR(20),
    create_time     TIMESTAMP DEFAULT NOW()
);

-- 9. 药品表
CREATE TABLE IF NOT EXISTS medicine (
    medicine_id VARCHAR(32)  PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    spec        VARCHAR(50),
    "usage"     VARCHAR(200),
    indication  TEXT,
    attention   TEXT,
    stock       INT,
    price       DECIMAL(10,2),
    create_time TIMESTAMP DEFAULT NOW()
);

-- 10. 处方表
CREATE TABLE IF NOT EXISTS prescription (
    prescription_id VARCHAR(32)  PRIMARY KEY,
    register_id     VARCHAR(32),
    patient_id      VARCHAR(32),
    doctor_id       VARCHAR(32),
    medicine_id     VARCHAR(32),
    patient_name    VARCHAR(20),
    doctor_name     VARCHAR(20),
    medicine_name   VARCHAR(50),
    spec            VARCHAR(50),
    "usage"         VARCHAR(200),
    num             INT,
    create_date     DATE,
    price           DECIMAL(10,2),
    pay_status      VARCHAR(20),
    create_time     TIMESTAMP DEFAULT NOW()
);

-- 11. 支付表
CREATE TABLE IF NOT EXISTS pay (
    pay_id       VARCHAR(32)  PRIMARY KEY,
    order_type   VARCHAR(20),
    business_id  VARCHAR(32),
    patient_id   VARCHAR(32),
    patient_name VARCHAR(20),
    total_amount DECIMAL(10,2),
    pay_status   VARCHAR(20),
    pay_time     TIMESTAMP
);

-- 12. 检查项目表
CREATE TABLE IF NOT EXISTS check_item (
    check_item_id  VARCHAR(32)  PRIMARY KEY,
    check_name     VARCHAR(50)  NOT NULL,
    check_type     VARCHAR(20),
    default_urgency INT,
    price          DECIMAL(10,2),
    status         SMALLINT DEFAULT 1,
    create_time    TIMESTAMP DEFAULT NOW()
);

-- 13. 检查房间表
CREATE TABLE IF NOT EXISTS check_room (
    room_id         VARCHAR(32)  PRIMARY KEY,
    room_name       VARCHAR(50)  NOT NULL,
    check_item_id   VARCHAR(32),
    check_item_name VARCHAR(50),
    max_queue       INT,
    current_queue   INT,
    allow_urgent    SMALLINT,
    status          SMALLINT DEFAULT 1,
    operator        VARCHAR(20),
    dept_id         VARCHAR(32),
    create_time     TIMESTAMP DEFAULT NOW()
);

-- 14. 检查分诊记录表
CREATE TABLE IF NOT EXISTS check_triage (
    triage_id        VARCHAR(32)  PRIMARY KEY,
    register_id      VARCHAR(32),
    patient_id       VARCHAR(32),
    check_item_id    VARCHAR(32),
    check_item_name  VARCHAR(50),
    urgency_level    INT          NOT NULL,
    assign_room_id   VARCHAR(32),
    assign_room_name VARCHAR(50),
    queue_no         INT,
    triage_time      TIMESTAMP,
    status           VARCHAR(20),
    create_time      TIMESTAMP DEFAULT NOW()
);

-- 15. AI 推理日志表
CREATE TABLE IF NOT EXISTS ai_inference_log (
    log_id        VARCHAR(32)  PRIMARY KEY,
    trace_id      VARCHAR(64)  NOT NULL,
    call_source   VARCHAR(30)  NOT NULL,
    model_key     VARCHAR(50)  NOT NULL,
    model_version VARCHAR(20)  NOT NULL,
    input_summary TEXT,
    output_summary TEXT,
    status        VARCHAR(20)  NOT NULL,
    duration_ms   INT,
    created_at    TIMESTAMP DEFAULT NOW(),
    patient_id    VARCHAR(32)
);

-- 16. AI 反馈样本表
CREATE TABLE IF NOT EXISTS ai_feedback_sample (
    sample_id        VARCHAR(32)  PRIMARY KEY,
    trace_id         VARCHAR(64)  NOT NULL,
    ai_output_json   JSON         NOT NULL,
    final_output_json JSON        NOT NULL,
    is_adopted       SMALLINT,
    diff_score       DECIMAL(5,2),
    label_tag        VARCHAR(50),
    used_for_training SMALLINT DEFAULT 0,
    created_at       TIMESTAMP DEFAULT NOW(),
    doctor_id        VARCHAR(32)
);

-- 17. AI 模型注册表
CREATE TABLE IF NOT EXISTS ai_model_registry (
    model_id      VARCHAR(32)  PRIMARY KEY,
    model_key     VARCHAR(50)  NOT NULL,
    version       VARCHAR(20)  NOT NULL,
    artifact_path VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    traffic_pct   INT DEFAULT 0,
    created_at    TIMESTAMP DEFAULT NOW(),
    update_time   TIMESTAMP DEFAULT NOW()
);
