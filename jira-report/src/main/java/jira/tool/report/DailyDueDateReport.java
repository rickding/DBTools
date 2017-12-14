package jira.tool.report;

import dbtools.common.utils.DateUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;

import java.util.Date;
import java.util.List;

public class DailyDueDateReport extends BaseReport {
    public DailyDueDateReport() {
        mapSheetName.put("data", "到期日没有或超过4周");
        mapSheetName.put("graph", "graph");
    }

    @Override
    public String getTemplateName() {
        return null; // "到期日没有或超过4周-template.xlsx";
    }

    public String getFileName() {
        return String.format("到期日没有或超过4周-%s.xlsx", DateUtils.format(new Date(), "MMdd"));
    }

    // Read story list from db
    protected List<Story> getStoryList() {
        return DBUtil.getNoDueDateStoryList();
    }
}
