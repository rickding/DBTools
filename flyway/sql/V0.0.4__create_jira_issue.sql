USE rms;

-- Jira issue
DROP TABLE IF EXISTS jira_issue;
CREATE TABLE IF NOT EXISTS jira_issue (
  id          BIGINT(32)   NOT NULL AUTO_INCREMENT,
  issue_key   VARCHAR(16)  NOT NULL,
  name        VARCHAR(256) NOT NULL,
  is_deleted  TINYINT(1)            DEFAULT 0,
  create_time TIMESTAMP    NOT NULL DEFAULT current_timestamp,
  update_time TIMESTAMP    NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8
  ROW_FORMAT = COMPACT;
