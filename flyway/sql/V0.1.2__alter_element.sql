USE rms;

-- element
ALTER TABLE `element`
  MODIFY COLUMN `status_id` BIGINT(32) DEFAULT NULL;
