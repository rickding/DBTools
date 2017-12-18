package ea.tool.db;

import dbtools.common.file.FileUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import ea.tool.api.EAFileUtil;
import jira.tool.ea.JiraProjectEnum;
import jira.tool.ea.PMOMeetingUtil;
import jira.tool.ea.JiraUserImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hello world!
 */
public class App {
    private static String File_Prefix = "";
    private static String File_Ext = ".eap";

    public static void main(String[] args) {
        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容确认文件夹");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容提交文件夹");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容提交文件夹\\商家线");
            add("..\\");
            add(".\\");
        }};

        if (args != null) {
            for (String arg : args) {
                if (!StrUtils.isEmpty(arg)) {
                    filePaths.add(arg);
                }
            }
        }

        // Process files
        List<String> projects = new ArrayList<String>();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            File[] files = FileUtils.findFiles(filePath, File_Prefix, File_Ext, null);
            if (!file.exists() || files == null || files.length <= 0) {
                continue;
            }

            for (File f : files) {
                // Check the date in file name
                String fileName = f.getName();
                filePath = f.getPath();
                if (!PMOMeetingUtil.needsToBeProcessed(f)) {
                    continue;
                }

                // Find project
                JiraProjectEnum project = JiraProjectEnum.findProject(fileName);
                if (project == null) {
                    continue;
                }

                // Read file and fill excel
                System.out.printf("Start to read: %s\n", fileName);
                List<String[]> records = EAFileUtil.readFile(filePath, new JiraUserImpl());

                // Save the elements
                EA2DBUtil.process(project, filePath, records);

                // Process
                projects.add(f.getPath());
            }
        }

        System.out.printf("Finished %d folder(s), %d file(s), start: %s, end: %s\r\n\r\n",
                filePaths.size(),
                projects.size(),
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
        for (String project : projects) {
            System.out.println(project);
        }
    }
}
