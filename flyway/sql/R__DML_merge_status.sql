USE rms;

SET @statusId = (SELECT id
                 FROM `status`
                 WHERE name = "Proposed");

UPDATE `element`
SET status_id = @statusId
WHERE status_id IN (SELECT DISTINCT id
                    FROM `status`
                    WHERE name NOT IN ('Proposed', 'Implemented', 'Approved', 'Mandatory', 'Validated'));

SET @aliases = (SELECT group_concat(name)
                FROM `status`
                WHERE name NOT IN ('Proposed', 'Implemented', 'Approved', 'Mandatory', 'Validated')
                GROUP BY 'all');

UPDATE `status`
SET aliases = concat_ws(',', aliases, @aliases)
WHERE id = @statusId;

DELETE FROM `status`
WHERE name NOT IN ('Proposed', 'Implemented', 'Approved', 'Mandatory', 'Validated');
