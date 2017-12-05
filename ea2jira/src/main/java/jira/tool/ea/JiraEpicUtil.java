package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.JiraUtil;
import jira.tool.db.model.Story;

import java.util.List;

public class JiraEpicUtil {
    private static List<Story> epicList = null;

    public static void updateEpic(String[] values) {
        if (ArrayUtils.isEmpty(values)) {
            return;
        }

        String epicName = values[EA2JiraHeaderEnum.EpicName.getIndex()];
        if (StrUtils.isEmpty(epicName) || StrUtils.isEmpty(epicName.trim())) {
            return;
        }

        // Query jira db to verify the epic name and get the epic key (link)
        synchronized ("updateEpic.getEpicList") {
            if (epicList == null) {
                epicList = JiraUtil.getEpicList();
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
