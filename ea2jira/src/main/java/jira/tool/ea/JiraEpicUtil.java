package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;

import java.util.List;

public class JiraEpicUtil {
    private static List<Story> epicList = null;

    public static Story findEpic(String epicName) {
        if (StrUtils.isEmpty(epicName)) {
            return null;
        }

        // Query jira db to verify the epic name and get the epic key (link)
        synchronized ("updateEpic.getEpicList") {
            if (epicList == null) {
                epicList = DBUtil.getEpicList();
            }
        }
        if (epicList == null || epicList.size() <= 0) {
            return null;
        }

        // Compare the epic name and key
        epicName = epicName.trim().toLowerCase();
        for (Story epic : epicList) {
            if (epicName.equalsIgnoreCase(epic.getTitle()) || epicName.equalsIgnoreCase(epic.getKey())) {
                return epic;
            }
        }
        return null;
    }

    public static void updateEpic(String[] values) {
        if (ArrayUtils.isEmpty(values)) {
            return;
        }

        String epicName = values[EA2JiraHeaderEnum.EpicName.getIndex()];
        if (StrUtils.isEmpty(epicName)) {
            return;
        }

        // Query jira db to verify the epic name and get the epic key (link)
        synchronized ("updateEpic.getEpicList") {
            if (epicList == null) {
                epicList = DBUtil.getEpicList();
            }
        }

        if (epicList == null || epicList.size() <= 0) {
            return;
        }

        // Compare the epic name and key
        epicName = epicName.trim().toLowerCase();
        boolean foundEpic = false;
        for (Story epic : epicList) {
            if (epicName.equalsIgnoreCase(epic.getTitle()) || epicName.equalsIgnoreCase(epic.getKey())) {
                values[EA2JiraHeaderEnum.EpicName.getIndex()] = epic.getTitle();
                values[EA2JiraHeaderEnum.EpicLink.getIndex()] = epic.getKey();
                foundEpic = true;
                break;
            }
        }

        if (!foundEpic) {
            values[EA2JiraHeaderEnum.EpicName.getIndex()] = null;
        }
    }

}
