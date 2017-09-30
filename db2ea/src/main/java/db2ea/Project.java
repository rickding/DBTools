package db2ea;

import db2ea.enums.ProjectEnum;
import db2ea.utils.StrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
    Map<String, List<EAItem>> dbListMap;
    private String path;
    private String code;
    private ProjectEnum projectEnum;

    public Project(Map<String, List<EAItem>> dbListMap, String path, String code) {
        this.dbListMap = dbListMap;
        this.path = path;
        this.code = code;

        findProjectEnum();
        if (projectEnum != null) {
            setProject(projectEnum.getName());
        }
    }

    public void checkAndMarkProject(Project project) {
        if (project == null || StrUtils.isEmpty(code)
                || StrUtils.isEmpty(project.code) || code.equals(project.code)
                || !ProjectEnum.isSameGroup(projectEnum, project.projectEnum)) {
            return;
        }

        // DB, Table, Field
        List<EAItem> dbList1 = getDBList(), dbList2 = project.getDBList();
        if (dbList1 == null || dbList1.size() <= 0 || dbList2 == null || dbList2.size() <= 0) {
            return;
        }

        for (EAItem db1 : dbList1) {
            if (db1 != null) {
                for (EAItem db2 : dbList2) {
                    db1.checkAndMarkProject(db2);
                }
            }
        }
    }

    private List<EAItem> getDBList() {
        if (dbListMap == null || dbListMap.size() <= 0) {
            return null;
        }

        List<EAItem> dbList = new ArrayList<EAItem>();
        for (Map.Entry<String, List<EAItem>> dbListEntry : dbListMap.entrySet()) {
            List<EAItem> tmp = dbListEntry.getValue();
            if (tmp != null && tmp.size() > 0) {
                dbList.addAll(tmp);
            }
        }

        return dbList;
    }

    public void saveToFile(EAWriter writer, boolean codeForExcel) {
        List<EAItem> dbList = getDBList();
        if (dbList == null || dbList.size() <= 0 || writer == null) {
            return;
        }

        for (EAItem db : dbList) {
            if (db != null) {
                db.saveToFile(writer, codeForExcel);
            }
        }
    }

    public void setProject(String name) {
        if (dbListMap == null || dbListMap.size() <= 0) {
            return;
        }

        for (Map.Entry<String, List<EAItem>> dbListEntry : dbListMap.entrySet()) {
            List<EAItem> dbList = dbListEntry.getValue();
            if (dbList == null || dbList.size() <= 0) {
                continue;
            }

            for (EAItem db : dbList) {
                if (db != null) {
                    db.setProject(name);
                }
            }
        }
    }

    public Map<String, List<EAItem>> getDbListMap() {
        return dbListMap;
    }

    public String getPath() {
        return path;
    }

    private void findProjectEnum() {
        if (projectEnum == null) {
            for (ProjectEnum pro : ProjectEnum.getList()) {
                if (code.indexOf(pro.getCode()) >= 0) {
                    projectEnum = pro;
                    break;
                }
            }
        }

        if (projectEnum == null) {
            System.out.printf("Can't find the project name: %s, %s\n", code, path);
        }
    }
}
