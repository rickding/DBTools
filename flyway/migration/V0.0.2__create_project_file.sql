USE rms;

-- table: project
DROP TABLE IF EXISTS project;
CREATE TABLE IF NOT EXISTS project (
  id          BIGINT(32)  NOT NULL AUTO_INCREMENT,
  name        VARCHAR(32) NOT NULL,
  aliases     VARCHAR(256)         DEFAULT NULL
  COMMENT 'separated by comma',
  is_deleted  TINYINT(1)           DEFAULT 0,
  create_time TIMESTAMP   NOT NULL DEFAULT current_timestamp,
  update_time TIMESTAMP   NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT;

-- table: file
DROP TABLE IF EXISTS file;
CREATE TABLE IF NOT EXISTS file (
  id          BIGINT(32)   NOT NULL AUTO_INCREMENT,
  name        VARCHAR(512) NOT NULL,
  project_id  BIGINT(32)   NOT NULL,
  is_deleted  TINYINT(1)            DEFAULT 0,
  create_time TIMESTAMP    NOT NULL DEFAULT current_timestamp,
  update_time TIMESTAMP    NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
  PRIMARY KEY (id),
  UNIQUE KEY index_project_file_key (id, project_id)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT;
