package jira.tool.ea;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EA2Jira {
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

                // Fill jira data
                String[] values = new String[headerMap.size()];
                int i = 0;

                for (Map.Entry<JiraHeaderEnum, EAHeaderEnum> map : headerMap.entrySet()) {
                    JiraHeaderEnum jiraHeader = map.getKey();

                    // Map the package as project
                    String value = row.getCell(map.getValue().getIndex()).getStringCellValue();
                    if (jiraHeader.getCode().equalsIgnoreCase(JiraHeaderEnum.Project.getCode())) {
                        value = packageMap.get(value);
                    }

                    values[i++] = value;
                }

                String team = "Jira";
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
        for (Map.Entry<JiraHeaderEnum, EAHeaderEnum> map : headerMap.entrySet()) {
            headers[i++] = map.getKey().getCode();
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
