package jira.tool.jira;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import ea.tool.api.EAStatusEnum;
import jira.tool.db.JiraResolveEnum;

import java.util.HashMap;
import java.util.Map;

public class StatusUtil {
    private static Map<JiraResolveEnum, EAStatusEnum> statusMap = new HashMap<JiraResolveEnum, EAStatusEnum>() {{
        put(JiraResolveEnum.Canceled, EAStatusEnum.Approved);
        put(JiraResolveEnum.Finished, EAStatusEnum.Approved);
        put(JiraResolveEnum.Resolved, EAStatusEnum.Approved);
    }};

    public static EAStatusEnum toEAStatus(String status) {
        if (StrUtils.isEmpty(status)) {
            return null;
        }

        status = status.trim();
        for (Map.Entry<JiraResolveEnum, EAStatusEnum> statusEntry : statusMap.entrySet()) {
            JiraResolveEnum result = statusEntry.getKey();
            if (result.getCode().equalsIgnoreCase(status)) {
                return statusEntry.getValue();
            }

            if (!ArrayUtils.isEmpty(result.getAliases())) {
                for (String res : result.getAliases()) {
                    if (res.equalsIgnoreCase(status)) {
                        return statusEntry.getValue();
                    }
                }
            }
        }
        return null;
    }
}
