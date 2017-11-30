package jira.tool.db;

import jira.tool.db.mapper.JiraMapper;
import jira.tool.db.model.Story;
import jira.tool.db.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraUtil {
    private static List<User> userList = null;

    public static List<User> getUserList() {
        synchronized ("getUserList") {
            if (userList == null || userList.size() <= 0) {
                userList = DB.getDb().getMapper(JiraMapper.class).getUserList();
            }
        }
        return userList;
    }

    public static List<Story> getStoryList() {
        List<Story> storyList = null;
        List<Story> guidList = null;
        List<Story> labelList = null;

        synchronized ("getStoryList") {
            JiraMapper mapper = DB.getDb().getMapper(JiraMapper.class);
            storyList = mapper.getStoryList();
            if (storyList != null && storyList.size() > 0) {
                guidList = mapper.getEAGUIDList();
                labelList = mapper.getPMOLabelList();
            }
        }

        // Combine the pmo label and EA GUID
        if (storyList != null && storyList.size() > 0 && (guidList != null && guidList.size() > 0 || labelList != null && labelList.size() > 0)) {
            Map<Long, Story> guidMap = toMap(guidList);
            Map<Long, Story> labelMap = toMap(labelList);

            for (Story story : storyList) {
                long id = story.getId();
                if (guidMap != null && guidMap.size() > 0 && guidMap.containsKey(id)) {
                    story.setEAGUID(guidMap.get(id).getEAGUID());
                }

                if (labelMap != null && labelMap.size() > 0 && labelMap.containsKey(id)) {
                    story.setLabel(labelMap.get(id).getLabel());
                }
            }
        }
        return storyList;
    }

    private static Map<Long, Story> toMap(List<Story> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<Long, Story> map = new HashMap<Long, Story>(list.size());
        for (Story item : list) {
            map.put(item.getId(), item);
        }
        return map;
    }
}
