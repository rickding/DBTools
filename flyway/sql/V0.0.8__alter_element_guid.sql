USE rms;

-- element_guid: add path
ALTER TABLE element_guid
  ADD COLUMN parent_path VARCHAR(512) NOT NULL
  AFTER guid;
