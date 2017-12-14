USE rms;

-- type
INSERT INTO type (`name`) VALUES ("Package"), ("Requirement");

-- status
INSERT INTO status (`name`, `aliases`) VALUES ("Proposed", "提议的");
INSERT INTO status (`name`) VALUES ("Implemented"), ("Approved"), ("Mandatory"), ("Validated");
