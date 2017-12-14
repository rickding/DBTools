USE rms;

-- Element
DROP TABLE IF EXISTS element;
CREATE TABLE IF NOT EXISTS element (
  id          BIGINT(32)  NOT NULL AUTO_INCREMENT,
  parent_id   BIGINT(32)           DEFAULT NULL,

  guid        VARCHAR(64) NOT NULL,
  file_id     BIGINT(32)  NOT NULL,
  name        VARCHAR(512) NOT NULL,
  type_id     BIGINT(32)  NOT NULL,
  status_id   BIGINT(32)  NOT NULL,

  pd_id       BIGINT(32)  NOT NULL,
  dev_id      BIGINT(32)           DEFAULT NULL,
  qa_id       BIGINT(32)           DEFAULT NULL,

  estimation  BIGINT(32)           DEFAULT NULL
  COMMENT 'man-minutes',
  pd_date     TIMESTAMP            DEFAULT 0,
  qa_date     TIMESTAMP            DEFAULT 0,
  due_date    TIMESTAMP            DEFAULT 0,

  notes       TEXT,
  tag         VARCHAR(256)         DEFAULT NULL,
  story_id    BIGINT(32)           DEFAULT NULL,
  epic_id     BIGINT(32)           DEFAULT NULL,

  is_deleted  TINYINT(1)           DEFAULT 0,
  create_time TIMESTAMP   NOT NULL DEFAULT current_timestamp,
  update_time TIMESTAMP   NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT;

-- Type
DROP TABLE IF EXISTS type;
CREATE TABLE IF NOT EXISTS type (
  id          BIGINT(32)  NOT NULL AUTO_INCREMENT,
  name        VARCHAR(32) NOT NULL,
  is_deleted  TINYINT(1)           DEFAULT 0,
  create_time TIMESTAMP   NOT NULL DEFAULT current_timestamp,
  update_time TIMESTAMP   NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT;

-- Status
DROP TABLE IF EXISTS status;
CREATE TABLE IF NOT EXISTS status (
  id          BIGINT(32)  NOT NULL AUTO_INCREMENT,
  name        VARCHAR(32) NOT NULL,
  is_deleted  TINYINT(1)           DEFAULT 0,
  create_time TIMESTAMP   NOT NULL DEFAULT current_timestamp,
  update_time TIMESTAMP   NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT;
