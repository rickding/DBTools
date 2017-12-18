USE rms;

-- Element GUID mapping in different files
DROP TABLE IF EXISTS element_guid;
CREATE TABLE IF NOT EXISTS element_guid (
  id          BIGINT(32)  NOT NULL AUTO_INCREMENT,
  element_id  BIGINT(32)  NOT NULL,
  guid        VARCHAR(64) NOT NULL,
  file_id     BIGINT(32)  NOT NULL,

  is_deleted  TINYINT(1)           DEFAULT 0,
  create_time TIMESTAMP   NOT NULL DEFAULT current_timestamp,
  update_time TIMESTAMP   NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT;

-- Element: remove two fields, which are in element_guid
ALTER TABLE element
  DROP COLUMN guid,
  DROP COLUMN file_id;
