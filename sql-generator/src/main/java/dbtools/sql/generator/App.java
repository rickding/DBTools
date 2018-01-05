package dbtools.sql.generator;

import dbtools.common.file.FileUtils;
import dbtools.common.file.FileWriter;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;
import jira.tool.db.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    private static String strToday = DateUtils.format(new Date(), "MMdd");
    private static String Sql_File_Name = "portfolio-%s.sql";

    private static String File_Ext = "";
    private static String Folder_name = "";

    public static void main(String[] args) {
        Date time_start = new Date();
        File file = new File("C:\\Work\\jira\\db\\portfolio.sql");

        String[] templates = FileUtils.read(file.getPath());
        if (ArrayUtils.isEmpty(templates)) {
            System.out.printf("Can't read template: %s\r\n", file.getPath());
        } else {
            List<String> teamList = new ArrayList<String>() {{
                add("订单线");
                add("交易线");
                add("基础架构");
                add("用户线");
                add("导购线");
                add("APP");
                add("智能平台");
                add("供应链");
                add("商家线");
                add("财务线");
                add("商品线");
            }};

            List<String> projectList = new ArrayList<String>() {{
                add("A-宜和");
                add("A-德升");
                add("A-来伊份");
                add("A-海航");
                add("A-京客隆");
                add("A-史泰博");
                add("A-欧普照明");
                add("A-平安租赁");
                add("A-晨光");
            }};

            // Read db
            List<User> userList = DBUtil.getTeamList();
            if (userList != null) {
                for (User user : userList) {
                    if (user != null && !StrUtils.isEmpty(user.getTeam()) && !teamList.contains(user.getTeam())) {
//                        teamList.add(user.getTeam());
                    }
                }
            }

            List<Story> customerList = DBUtil.getCustomerOptionListOnlyEnabled();
            if (customerList != null) {
                for (Story customer : customerList) {
                    if (customer != null && !StrUtils.isEmpty(customer.getCustomer()) && !projectList.contains(customer.getCustomer())) {
//                        projectList.add(customer.getCustomer());
                    }
                }
            }

            // Generate sql
            int weeklyHours = (int) (40.0 / projectList.size());
            List<String> sqlList = new ArrayList<String>() {{
                add("use jiradb;");
            }};

            int count = 0;
            for (String team : teamList) {
                for (String project : projectList) {
                    sqlList.add("");
                    sqlList.add("");
                    sqlList.add(String.format("-- %d, %s, %s", count++, team, project));
                    SqlGenerator.process(sqlList, templates, team, project, weeklyHours);
                }
            }

            // Save sql
            if (sqlList != null && sqlList.size() > 1) {
                String outputFileName = FileUtils.getOutputFileName(file.getParentFile(), "", File_Ext, String.format(Sql_File_Name, strToday), Folder_name);
                FileWriter writer = new FileWriter(outputFileName);

                if (writer.open()) {
                    String[] sqlArray = new String[sqlList.size()];
                    sqlList.toArray(sqlArray);
                    writer.writeLines(sqlArray);
                }
                writer.close();
            }
        }

        System.out.printf("Finished, start: %s, end: %s\r\n",
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
    }
}
