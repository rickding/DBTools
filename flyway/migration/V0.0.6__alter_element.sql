USE rms;

-- Element: pd_id can be null
ALTER TABLE element
  MODIFY COLUMN pd_id BIGINT(32) DEFAULT NULL;
