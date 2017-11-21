package jira.tool.ea;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

public class EA2Jira {
    private static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");

    private static Map<String, String> findPackages(XSSFSheet sheet, int rowStart, int rowEnd) {
        if (sheet == null || rowStart < 0 || rowStart > rowEnd) {
            return null;
        }

        Map<String, String> packageMap = new HashMap<String, String>();
        while (rowStart <= rowEnd) {
            Row row = sheet.getRow(rowStart++);
            if (row != null) {
                // Only process package as project
                if (!EATypeEnum.Package.getCode().equalsIgnoreCase(row.getCell(EAHeaderEnum.Type.getIndex()).getStringCellValue())) {
                    continue;
                }

                String name = row.getCell(EAHeaderEnum.Name.getIndex()).getStringCellValue();
                String key = row.getCell(EAHeaderEnum.Key.getIndex()).getStringCellValue();

                if (StrUtils.isEmpty(name) || StrUtils.isEmpty(key)) {
                    System.out.printf("Error when empty package and key: %s, %s\n", name, key);
                }

                packageMap.put(key, name);
            }
        }

        return packageMap;
    }

    private static String processValue(JiraHeaderEnum jiraHeaderEnum, String value) {
        if (jiraHeaderEnum == null || StrUtils.isEmpty(jiraHeaderEnum.getCode())) {
            return value;
        }

        String jiraHeader = jiraHeaderEnum.getCode();

        // Find the user name
        for (JiraHeaderEnum tmp : new JiraHeaderEnum[] {JiraHeaderEnum.Developer, JiraHeaderEnum.Owner, JiraHeaderEnum.PM}) {
            if (jiraHeader.equalsIgnoreCase(tmp.getCode())) {
                JiraUserEnum user = JiraUserEnum.findUser(value);
                if (user == null) {
                    System.out.printf("Error when find user: %s\n", value);
                } else {
                    return user.getName();
                }
            }
        }

        // Estimation
        if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Estimation.getCode())) {
            int base = 8 * 3600; // default as a day
            double v = 1.0; // default as one
            if (!StrUtils.isEmpty(value)) {
                try {
                    if (value.endsWith("h")) {
                        v = Double.valueOf(value.substring(0, value.length() - 1));
                        base = 3600;
                    } else if (value.endsWith("d")) {
                        v = Double.valueOf(value.substring(0, value.length() - 1));
                        base = 8 * 3600;
                    } else {
                        v = Double.valueOf(value);
                    }
                } catch (Exception e) {
                    System.out.printf("Error when process value: %s, %s\n", jiraHeader, value);
                }
                return String.format("%d", (int)(v * base));
            }
        }

        // DueDate
        for (JiraHeaderEnum tmp : new JiraHeaderEnum[] {JiraHeaderEnum.DueDate, JiraHeaderEnum.QAStartDate, JiraHeaderEnum.QAFinishDate}) {
            if (jiraHeader.equalsIgnoreCase(tmp.getCode())) {
                String[] formats = new String[] {
                        "yyyyMMdd", "yyyy.MM.dd", "yyyy-MM-dd", "yyyy/MM/dd",
                        "yyMMdd", "yy.MM.dd", "yy-MM-dd", "yy/MM/dd",
                        "MMdd", "MM.dd", "MM-dd", "MM/dd"
                };
                for (String format : formats) {
                    try {
                        Date date = DateUtils.parse(value, format, false);
                        if (date != null) {
                            // Adjust the year if it's not set
                            if (format.length() < 8) {
                                String strDate = DateUtils.format(date, "MM-dd");
                                int year = Integer.valueOf(DateUtils.format(today, "yyyy"));
                                int month = Integer.valueOf(DateUtils.format(today, "MM"));
                                if (month >= 12 && strDate.compareTo(DateUtils.format(today, "MM-dd")) < 0) {
                                    year++;
                                }
                                date = DateUtils.parse(String.format("%4d-%s", year, strDate), "yyyy-MM-dd");
                            }

                            // QA start 2 days earlier
                            if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.QAStartDate.getCode())) {
                                int days = DateUtils.diffDays(date, today);
                                if (days > 3) {
                                    days = 2;
                                } else if (days > 1) {
                                    days = 1;
                                } else {
                                    days = 0;
                                }

                                if (days > 0) {
                                    date = DateUtils.adjustDate(date, -days);
                                }
                            }

                            // Format date
                            value = DateUtils.format(date, JiraHeaderEnum.Jira_Date_Format);
                            return value;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

        // The specified values
        if (JiraHeaderEnum.JiraHeaderValueMap.containsKey(jiraHeaderEnum)) {
            return jiraHeaderEnum.JiraHeaderValueMap.get(jiraHeaderEnum);
        }

        return value;
    }

    public static void process(XSSFSheet sheet, XSSFWorkbook wb) {
        if (sheet == null || wb == null) {
            return;
        }

        int rowStart = sheet.getFirstRowNum();
        int rowEnd = sheet.getLastRowNum();

        // Headers
        EAHeaderEnum.fillIndex(ExcelUtil.getRowValues(sheet, rowStart));
        rowStart++;

        // Find package firstly
        Map<String, String> packageMap = findPackages(sheet, rowStart, rowEnd);
        Map<JiraHeaderEnum, EAHeaderEnum> headerMap = JiraHeaderEnum.JiraEAHeaderMap;
        JiraHeaderEnum[] jiraHeaders = JiraHeaderEnum.getSortedHeaders();

        // Data
        Map<String, List<String[]>> jiraValues = new HashMap<String, List<String[]>>();

        while (rowStart <= rowEnd) {
            Row row = sheet.getRow(rowStart++);
            if (row != null) {
                // Only process requirement as story
                if (!EATypeEnum.Requirement.getCode().equalsIgnoreCase(row.getCell(EAHeaderEnum.Type.getIndex()).getStringCellValue())
                        || !EAStatusEnum.isProcessStatus(row.getCell(EAHeaderEnum.Status.getIndex()).getStringCellValue())) {
                    continue;
                }

                // Fill jira dataMap.Entry<JiraHeaderEnum, EAHeaderEnum> map : headerMap.entrySet()
                String team = "Jira";
                String[] values = new String[headerMap.size()];
                int i = 0;

                for (JiraHeaderEnum jiraHeaderEnum : jiraHeaders) {
                    if (jiraHeaderEnum == null || StrUtils.isEmpty(jiraHeaderEnum.getCode())) {
                        continue;
                    }

                    EAHeaderEnum eaHeader = headerMap.get(jiraHeaderEnum);
                    String value = eaHeader == null ? null : row.getCell(eaHeader.getIndex()).getStringCellValue();
                    value = processValue(jiraHeaderEnum, value);

                    // Special values
                    String jiraHeader = jiraHeaderEnum.getCode();
                    if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Project.getCode())) {
                        // Map the package as project
                        value = packageMap.get(value);
                    } else if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Developer.getCode())) {
                        // Team of the developer
                        JiraUserEnum user = JiraUserEnum.findUser(value);
                        if (user == null) {
                            System.out.printf("Error when find user: %s\n", value);
                        } else {
                            team = user.getTeam();
                        }
                    }

                    values[i++] = value;
                }

                // Group as team
                List<String[]> stroies = jiraValues.get(team);
                if (stroies == null) {
                    stroies = new ArrayList<String[]>();
                    jiraValues.put(team, stroies);
                }
                stroies.add(values);
            }
        }

        String[] headers = new String[headerMap.size()];
        int i = 0;
        for (JiraHeaderEnum jiraHeader : jiraHeaders) {
            headers[i++] = jiraHeader.getCode();
        }

        // Write to excel
        for (Map.Entry<String, List<String[]>> teamStories : jiraValues.entrySet()) {
            sheet = ExcelUtil.getOrCreateSheet(wb, teamStories.getKey());
            if (sheet == null) {
                System.out.printf("Error when write team stories: %s\n", teamStories.getKey());
                continue;
            }

            // headers
            int row = 0;
            ExcelUtil.fillRow(sheet, row++, headers);

            // data
            for (String[] story : teamStories.getValue()) {
                ExcelUtil.fillRow(sheet, row++, story);
            }
        }
    }
}
