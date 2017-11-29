package jira.tool.ea;

import dbtools.common.utils.StrUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JiraKeyUtil {
    private static Pattern pattern = Pattern.compile("[A-Z]+-[1-9][0-9]*");
    private static Pattern patternSplit = Pattern.compile("[A-Z]+-");

    public static boolean isValid(String issueKey) {
        if (StrUtils.isEmpty(issueKey)) {
            return false;
        }

        issueKey = issueKey.trim().toUpperCase();
        Matcher matcher = pattern.matcher(issueKey);
        if (matcher.find()) {
            String tmp = matcher.group();
            if (!issueKey.equalsIgnoreCase(tmp)) {
                return false;
            }
        } else {
            return false;
        }

        String[] arr = patternSplit.split(issueKey);
        if (arr != null && arr.length == 2) {
            Integer id = Integer.valueOf(arr[1]);
            return id > 0;
        }
        return false;
    }
}
