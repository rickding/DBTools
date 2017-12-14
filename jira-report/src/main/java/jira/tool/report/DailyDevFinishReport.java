package jira.tool.report;

import dbtools.common.utils.DateUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;

import java.util.Date;
import java.util.List;

public class DailyDevFinishReport extends BaseReport {
    public DailyDevFinishReport() {
        mapSheetName.put("data", "完成开发待提测");
        mapSheetName.put("graph", "graph");
    }

    @Override
    public String getTemplateName() {
        return null; // "完成开发待提测-template.xlsx";
    }

    public String getFileName() {
        return String.format("完成开发待提测-%s.xlsx", DateUtils.format(new Date(), "MMdd"));
    }

    // Read story list from db
    protected List<Story> getStoryList() {
        return DBUtil.getDevelopedStoryList();
    }
}
