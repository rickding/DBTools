USE rms;

ALTER TABLE `element`
  ADD COLUMN project_id BIGINT(32) NOT NULL
  AFTER parent_id;

UPDATE `element`
SET `element`.project_id = (SELECT `file`.project_id
                            FROM `file`, `element_guid`
                            WHERE `file`.id = `element_guid`.file_id AND `element`.id = `element_guid`.element_id);
