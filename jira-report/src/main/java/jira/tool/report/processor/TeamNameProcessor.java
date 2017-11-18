package jira.tool.report.processor;

import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.HashMap;
import java.util.Map;

public class TeamNameProcessor implements ValueProcessor {
    // Map the project name to team name
    protected Map<String, String> values = new HashMap<String, String>() {{
        put("Android", "APP");
        put("IOS", "APP");

        put("WMS", "供应链");
        put("史泰博", "供应链");
        put("互联网+", "供应链");

        put("分销", "商家线");
        put("用户线", "商家线");
        put("雨燕平台", "基础架构");
        put("应用架构", "应用架构(one-instance)");
    }};

    public boolean accept(String header) {
        return !StrUtils.isEmpty(header) && header.equalsIgnoreCase(HeaderProcessor.teamNameHeader.getName());
    }

    public void process(String value, Cell cell) {
        if (cell == null) {
            return;
        }

        if (values.containsKey(value)) {
            cell.setCellValue(values.get(value));
        } else {
            cell.setCellValue(value);
        }
    }
}
