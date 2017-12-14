USE rms;

-- status
ALTER TABLE `status`
  ADD COLUMN `aliases` VARCHAR(256) DEFAULT NULL
COMMENT 'separated by comma'
  AFTER `name`;
