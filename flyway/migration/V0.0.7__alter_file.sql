USE rms;

-- Element: pd_id can be null
ALTER TABLE file
  ADD COLUMN path VARCHAR(512) NOT NULL
  AFTER name,
  MODIFY COLUMN name VARCHAR(64) NOT NULL;
