USE rms;

ALTER TABLE `element`
  ADD COLUMN `path` VARCHAR(1024) DEFAULT NULL
  AFTER parent_id;

UPDATE `element`
SET `element`.path = (SELECT `element_guid`.path
                      FROM `element_guid`
                      WHERE `element_guid`.element_id = `element`.id);

ALTER TABLE `element_guid`
  DROP COLUMN `path`;
