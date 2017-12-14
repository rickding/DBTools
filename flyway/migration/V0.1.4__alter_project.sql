USE rms;

ALTER TABLE `project`
  ADD COLUMN `sub_project` VARCHAR(32) DEFAULT NULL
  AFTER `aliases`;
