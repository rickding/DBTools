package com.rms.db;

import com.rms.db.mapper.ElementGuidMapper;
import com.rms.db.mapper.ElementMapper;
import com.rms.db.mapper.ElementMapperEx;
import com.rms.db.mapper.FileMapper;
import com.rms.db.mapper.JiraIssueMapper;
import com.rms.db.mapper.ProjectMapper;
import com.rms.db.mapper.StatusMapper;
import com.rms.db.mapper.TypeMapper;
import com.rms.db.mapper.UserMapper;
import com.rms.db.model.Element;
import com.rms.db.model.ElementEx;
import com.rms.db.model.ElementGuid;
import com.rms.db.model.File;
import com.rms.db.model.JiraIssue;
import com.rms.db.model.Project;
import com.rms.db.model.Status;
import com.rms.db.model.Type;
import com.rms.db.model.User;

import javax.annotation.Resource;
import java.util.List;

public class DBUtil {
    public static List<ElementEx> getElementList() {
        List<ElementEx> ret = null;
        synchronized ("getElementList") {
            ret = DB.getDb().getMapper(ElementMapperEx.class).selectAll();

            if (ret != null && ret.size() > 0) {
                // Read more info
                ElementUtil.updateElementInfo(ret, getProjectList(), getElementGuidList(), getFileList());
            }
        }
        return ret;
    }

    public static ElementEx addElement(ElementEx item) {
        return addElement(item, true);
    }

    public static ElementEx addElement(final ElementEx item, boolean commit) {
        if (item == null) {
            return null;
        }

        int count = DB.getDb().getMapper(ElementMapper.class).insert(item);
        if (count > 0) {
            // Fill element-guid
            addElementGuid(new ElementGuid(){{
                setElementId(item.getId());
                setGuid(item.getGuidList().get(0));
                setFileId(item.getFileList().get(0).getId());
            }}, false);

            if (commit) {
                DB.getDb().commit();
            }
        }
        return count <= 0 ? null : item;
    }

    public static List<ElementGuid> getElementGuidList() {
        List<ElementGuid> ret = null;
        synchronized ("getElementGuidList") {
            ret = DB.getDb().getMapper(ElementGuidMapper.class).selectAll();
        }
        return ret;
    }

    public static ElementGuid addElementGuid(ElementGuid item) {
        return addElementGuid(item, true);
    }

    public static ElementGuid addElementGuid(ElementGuid item, boolean commit) {
        if (item == null) {
            return null;
        }
        int count = DB.getDb().getMapper(ElementGuidMapper.class).insert(item);
        if (count > 0 && commit) {
            DB.getDb().commit();
        }
        return count > 0 ? item : null;
    }

    public static List<File> getFileList() {
        List<File> ret = null;
        synchronized ("getFileList") {
            ret = DB.getDb().getMapper(FileMapper.class).selectAll();
        }
        return ret;
    }

    public static File addFile(File item) {
        return addFile(item, true);
    }

    public static File addFile(File item, boolean commit) {
        if (item == null) {
            return null;
        }
        int count = DB.getDb().getMapper(FileMapper.class).insert(item);
        if (count > 0 && commit) {
            DB.getDb().commit();
        }
        return count > 0 ? item : null;
    }

    public static List<Project> getProjectList() {
        List<Project> ret = null;
        synchronized ("getProjectList") {
            ret = DB.getDb().getMapper(ProjectMapper.class).selectAll();
        }
        return ret;
    }

    public static Project addProject(Project item) {
        return addProject(item, true);
    }

    public static Project addProject(Project item, boolean commit) {
        if (item == null) {
            return null;
        }
        int count = DB.getDb().getMapper(ProjectMapper.class).insert(item);
        if (count > 0 && commit) {
            DB.getDb().commit();
        }
        return count > 0 ? item : null;
    }

    public static List<Type> getTypeList() {
        List<Type> ret = null;
        synchronized ("getTypeList") {
            ret = DB.getDb().getMapper(TypeMapper.class).selectAll();
        }
        return ret;
    }

    public static Type addType(Type item) {
        return addType(item, true);
    }

    public static Type addType(Type item, boolean commit) {
        if (item == null) {
            return null;
        }
        int count = DB.getDb().getMapper(TypeMapper.class).insert(item);
        if (count > 0 && commit) {
            DB.getDb().commit();
        }
        return count > 0 ? item : null;
    }

    public static List<Status> getStatusList() {
        List<Status> ret = null;
        synchronized ("getStatusList") {
            ret = DB.getDb().getMapper(StatusMapper.class).selectAll();
        }
        return ret;
    }

    public static Status addStatus(Status item) {
        return addStatus(item, true);
    }

    public static Status addStatus(Status item, boolean commit) {
        if (item == null) {
            return null;
        }
        int count = DB.getDb().getMapper(StatusMapper.class).insert(item);
        if (count > 0 && commit) {
            DB.getDb().commit();
        }
        return count > 0 ? item : null;
    }

    public static List<JiraIssue> getJiraIssueList() {
        synchronized ("getJiraIssueList") {
            return DB.getDb().getMapper(JiraIssueMapper.class).selectAll();
        }
    }

    public static JiraIssue addJiraIssue(JiraIssue item) {
        return addJiraIssue(item, true);
    }

    public static JiraIssue addJiraIssue(JiraIssue item, boolean commit) {
        if (item == null) {
            return null;
        }
        int count = DB.getDb().getMapper(JiraIssueMapper.class).insert(item);
        if (count > 0 && commit) {
            DB.getDb().commit();
        }
        return count > 0 ? item : null;
    }

    public static List<User> getUserList() {
        List<User> ret = null;
        synchronized ("getUserList") {
            ret = DB.getDb().getMapper(UserMapper.class).selectAll();
        }
        return ret;
    }

    public static User addUser(User item) {
        return addUser(item, true);
    }

    public static User addUser(User item, boolean commit) {
        if (item == null) {
            return null;
        }
        int count = DB.getDb().getMapper(UserMapper.class).insert(item);
        if (count > 0 && commit) {
            DB.getDb().commit();
        }
        return count > 0 ? item : null;
    }

    @Resource
    private DBService dbService;

    private static DBUtil dbUtil = null;

    public static DBService getService() {
        synchronized ("get service") {
            if (dbUtil == null) {
                dbUtil = new DBUtil();
            }
        }
        return dbUtil.dbService;
    }
}
