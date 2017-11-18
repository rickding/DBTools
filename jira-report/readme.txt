SELECT  t.ID AS `issuenum`,t.resolution as resolution,s.pname AS `jiraproject`,CONCAT(s.pkey,'-',t.issuenum) AS keyid,t.SUMMARY as summary
  ,y.customvalue AS project,d.pname AS `status`
  ,f.last_name AS reporter,g.last_name AS assignee,coder.last_name as coder
  ,h.pname AS resolution,cfv1.DATEVALUE as planTestDate,t.DUEDATE AS planReleaseDate
  ,cfv2.DATEVALUE as realTestDate,cfv3.DATEVALUE as realReleaseDate
  ,SUM(devlog.timeworked)/3600 as timeSpent,sumSubtask.timeSpent/3600 as subtaskTimeSpent,t.TIMEORIGINALESTIMATE/3600 as timeEstimate
FROM  jiraissue t
  INNER JOIN project s ON(t.PROJECT = s.ID and (t.issuetype = 10001 or t.issuetype = 11500) and (t.resolutiondate >= "2017-04-01" or t.issuestatus != 6))
  INNER JOIN issuetype c ON(t.issuetype=c.ID)
  INNER JOIN issuestatus d ON(t.issuestatus=d.ID)
  LEFT JOIN cwd_user f ON(t.REPORTER=f.user_name AND f.directory_id=10000)-- 人员账号遗留问题，directory_id=10000是邮箱账号
  LEFT JOIN cwd_user g  ON(t.ASSIGNEE=g.user_name AND g.directory_id=10000)
  LEFT JOIN resolution h ON(t.RESOLUTION=h.ID)
  LEFT JOIN (
   SELECT worklog.issueid as issueid,dev.id as userid,dev.username as username,worklog.timeworked as timeworked
   FROM worklog Inner JOIN( 
    Select user.user_name as id,user.last_name as username  from cwd_user user 
     Inner Join cwd_membership member ON (member.parent_name = "开发组" and member.child_name = user.user_name)
     group by (user.user_name)
    ) dev ON (dev.id = worklog.author) 
  ) devlog on (devlog.issueid = t.id)
        
  LEFT JOIN (
    SELECT m.issue AS issueid,x.customvalue AS customvalue
    From customfieldvalue m
    INNER JOIN customfieldoption x ON(m.CUSTOMFIELD = x.CUSTOMFIELD AND m.STRINGVALUE = x.ID AND m.customfield = 10804)
  ) y ON  (t.id=y.issueid) 
  
  LEFT JOIN customfieldvalue cfv1 on (cfv1.issue = t.id  and cfv1.customfield = 11034) -- 计划送测日期
  LEFT JOIN customfieldvalue cfv2 on (cfv2.issue = t.id and cfv2.customfield = 11700) -- 实际送测日期
  LEFT JOIN customfieldvalue cfv3 on (cfv3.issue = t.id and cfv3.customfield = 11701) -- 实际上线日期
  
  LEFT JOIN (
   SELECT customfieldvalue.issue as issue,cwd_user.last_name as last_name
   FROM customfieldvalue INNER JOIN cwd_user ON (customfieldvalue.customfield = 11033 and customfieldvalue.stringvalue = cwd_user.user_name and cwd_user.directory_id = 10000)
  ) coder ON (coder.issue  = t.id)
  
  
  LEFT JOIN (
   SELECT SUM(sub.timespent) as timespent,sum(sub.timespent2) as timespent2,issuelink.SOURCE AS parent
   FROM ( 
    SELECT t2.timespent as timespent2,SUM(devlog.timeworked) as timespent,t2.id 
                From jiraissue t2
                INNER JOIN 
                (
     SELECT worklog.issueid as issueid,dev.id as userid,dev.username as username,worklog.timeworked as timeworked
     FROM worklog Inner JOIN( 
      Select user.user_name as id,user.last_name as username  from cwd_user user 
       Inner Join cwd_membership member ON (member.parent_name = "开发组" and member.child_name = user.user_name)
       group by (user.user_name)
      ) dev ON (dev.id = worklog.author) 
     ) devlog on (devlog.issueid = t2.id and t2.issuetype = 10101)
     GROUP BY(t2.id)
                ) sub 
    LEFT JOIN issuelink ON (issuelink.destination = sub.id and issuelink.linktype = 10100)
    GROUP BY(issuelink.SOURCE)
  )sumSubtask on (sumSubtask.parent = t.id)
GROUP BY t.ID   
ORDER BY t.created DESC