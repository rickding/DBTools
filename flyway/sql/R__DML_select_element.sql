USE rms;

SELECT
  element.id,
  parent_id,
  element.project_id,
  project.name                    AS project_name,
  element.path,
  element.name,
  status_id,
  status.name                     AS status_name,
  story.issue_key                 AS story_key,
  story.name                      AS story_name,
  epic.name                       AS epic_name,
  epic.issue_key                  AS epic_key,
  pd_id,
  dev_id,
  qa_id,
  pd.name                         AS pd_name,
  dev.name                        AS dev_name,
  qa.name                         AS qa_name,
  estimation,
  estimation / (8.0 * 3600)       AS man_day,
  due_date,
  qa_date,
  pd_date,
  type_id,
  type.name                       AS type_name,
  notes,
  tag,
  element.is_deleted,
  element.create_time,
  element.update_time,
  group_concat(element_guid.guid) AS guid,
  group_concat(file.name)         AS file_name,
  group_concat(file.path)         AS file_path
FROM `element`
  LEFT JOIN element_guid ON element.id = element_guid.element_id
  LEFT JOIN file ON file.id = element_guid.file_id
  LEFT JOIN `project` ON element.project_id = project.id
  LEFT JOIN `type` ON element.type_id = type.id
  LEFT JOIN `status` ON element.status_id = status.id
  LEFT JOIN `user` AS pd ON element.pd_id = pd.id
  LEFT JOIN `user` AS dev ON element.dev_id = dev.id
  LEFT JOIN `user` AS qa ON element.qa_id = qa.id
  LEFT JOIN `jira_issue` AS story ON element.story_id = story.id
  LEFT JOIN `jira_issue` AS epic ON element.epic_id = epic.id
WHERE element.is_deleted IS NULL OR element.is_deleted = 0
GROUP BY element.id
ORDER BY pd_date DESC;
