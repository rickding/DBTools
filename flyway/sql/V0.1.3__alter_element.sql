USE rms;

-- element
ALTER TABLE `element`
  MODIFY COLUMN `name` VARCHAR(1024) NOT NULL;
