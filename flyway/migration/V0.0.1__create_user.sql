USE rms;

-- table: user
DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user (
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
