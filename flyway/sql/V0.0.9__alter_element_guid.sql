USE rms;

-- element_guid: add path
ALTER TABLE element_guid
  CHANGE COLUMN parent_path path VARCHAR(512) NOT NULL;
