package jira.tool.db;

import dbtools.common.utils.StrUtils;
import jira.tool.db.mapper.JiraMapper;
import jira.tool.db.model.Story;
import jira.tool.db.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {
    private static List<User> userList = null;
    private static List<User> teamList = null;
    private static List<User> teamMemberCountList = null;
    private static Map<Long, Story> customerOptionMap = null;

    public static List<User> getUserList() {
        synchronized ("getUserList") {
            if (userList == null || userList.size() <= 0) {
                userList = DB.getDb().getMapper(JiraMapper.class).getUserList();
            }
        }
        return userList;
    }

    public static List<User> getTeamList() {
        synchronized ("getTeamList") {
            if (teamList == null || teamList.size() <= 0) {
                teamList = DB.getDb().getMapper(JiraMapper.class).getTeamList();
            }
        }
        return teamList;
    }

    public static List<User> getTeamMembersCountList() {
        synchronized ("getTeamMembersCountList") {
            if (teamMemberCountList == null || teamMemberCountList.size() <= 0) {
                teamMemberCountList = DB.getDb().getMapper(JiraMapper.class).getTeamMembersCountList();
            }
        }
        return teamMemberCountList;
    }

    public static List<Story> getNoDueDateStoryList() {
        List<Story> storyList = null;
        synchronized ("getNoDueDateStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getNoDueDateStoryList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> getDevelopedStoryList() {
        List<Story> storyList = null;
        synchronized ("getDevelopedStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getDevelopedStoryList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> getQAStoryList() {
        List<Story> storyList = null;
        synchronized ("getQAStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getQAStoryList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> getUnDevelopedStoryList() {
        List<Story> storyList = null;
        synchronized ("getUnDevelopedStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getUnDevelopedStoryList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> getReleasePlanStoryList() {
        List<Story> storyList = null;
        synchronized ("getReleasePlanStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getReleasePlanStoryList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> getStartPlanStoryList() {
        List<Story> storyList = null;
        synchronized ("getStartPlanStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getStartPlanStoryList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> getReleasedStoryList() {
        List<Story> storyList = null;
        synchronized ("getReleasedStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getReleasedStoryList();
        }
        return updateStoryList(storyList, true);
    }

    public static List<Story> getPMOStoryList() {
        List<Story> storyList = null;
        synchronized ("getPMOStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getPMOStoryList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> getStoryList() {
        List<Story> storyList = null;
        synchronized ("getStoryList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getStoryList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> getEpicList() {
        List<Story> storyList = null;
        synchronized ("getEpicList") {
            storyList = DB.getDb().getMapper(JiraMapper.class).getEpicList();
        }
        return updateStoryList(storyList);
    }

    public static List<Story> updateStoryList(List<Story> storyList) {
        return updateStoryList(storyList, false);
    }

    public static List<Story> updateStoryList(List<Story> storyList, boolean checkReleaseDate) {
        if (storyList == null || storyList.size() <= 0) {
            return storyList;
        }

        // Combine the information, which is better than querying db from multiple tables.
        JiraMapper mapper = DB.getDb().getMapper(JiraMapper.class);
        Map<Long, Story> guidMap = toMap(mapper.getEAGUIDList());
        Map<Long, Story> labelMap = toMap(mapper.getPMOLabelList());
        Map<Long, Story> startDateMap = toMap(mapper.getStartDateList());
        Map<Long, Story> releaseDateMap = toMap(mapper.getReleaseDateList());
        Map<Long, Story> customer = toMap(getCustomerList());

        for (Story story : storyList) {
            long id = story.getId();
            if (guidMap != null && guidMap.containsKey(id)) {
                story.setEAGUID(guidMap.get(id).getEAGUID());
            }

            if (labelMap != null && labelMap.containsKey(id)) {
                story.setLabel(labelMap.get(id).getLabel());
            }

            if (startDateMap != null && startDateMap.containsKey(id)) {
                story.setStartDate(startDateMap.get(id).getStartDate());
            }

            if (releaseDateMap != null && releaseDateMap.containsKey(id)) {
                story.setReleaseDate(releaseDateMap.get(id).getReleaseDate());
            }

            if (customer != null && customer.containsKey(id)) {
                story.setCustomer(customer.get(id).getCustomer());
            }

            // Update release date if it's closed but has no value.
            if (checkReleaseDate && story.getReleaseDate() == null) {
                if ("YuYan".equalsIgnoreCase(story.getProjectKey()) || "史泰博".equalsIgnoreCase(story.getProjectKey())) {
                    story.setReleaseDate(story.getResolveDate());
                }
            }
        }
        return storyList;
    }

    public static List<Story> getCustomerOptionListOnlyEnabled() {
        synchronized ("getCustomerOptionListOnlyEnabled") {
            return DB.getDb().getMapper(JiraMapper.class).getCustomerOptionListOnlyEnabled();
        }
    }

    public static List<Story> getCustomerOptionList() {
        synchronized ("getCustomerOptionList") {
            return DB.getDb().getMapper(JiraMapper.class).getCustomerOptionList();
        }
    }

    /**
     * Return the customer list, which has been updated with customer option.
     * @return
     */
    public static List<Story> getCustomerList() {
        synchronized ("getCustomerOptionList") {
            if (customerOptionMap == null || customerOptionMap.size() <= 0) {
                List<Story> customerOptionList = DB.getDb().getMapper(JiraMapper.class).getCustomerOptionList();
                customerOptionMap = toMap(customerOptionList);
            }
        }

        List<Story> customerList = DB.getDb().getMapper(JiraMapper.class).getCustomerList();
        if (customerList == null || customerList.size() <= 0) {
            return null;
        }

        for (Story customer : customerList) {
            long customerId = customer.getCustomerId();
            if (customerId > 0 && customerOptionMap.containsKey(customerId)) {
                customer.setCustomer(customerOptionMap.get(customerId).getCustomer());
            } else {
                System.out.printf("Error when find customer option: %d\r\n", customerId);
            }
        }
        return customerList;
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
