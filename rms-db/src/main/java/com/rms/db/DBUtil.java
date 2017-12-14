package com.rms.db;

import com.rms.db.mapper.ElementGuidMapper;
import com.rms.db.mapper.ElementMapper;
import com.rms.db.mapper.FileMapper;
import com.rms.db.mapper.ProjectMapper;
import com.rms.db.mapper.StatusMapper;
import com.rms.db.mapper.TypeMapper;
import com.rms.db.mapper.UserMapper;
import com.rms.db.model.Element;
import com.rms.db.model.ElementGuid;
import com.rms.db.model.File;
import com.rms.db.model.Project;
import com.rms.db.model.Status;
import com.rms.db.model.Type;
import com.rms.db.model.User;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {
    public static List<Element> getElementList() {
        List<Element> ret = null;
        synchronized ("getElementList") {
            ret = DB.getDb().getMapper(ElementMapper.class).selectAll();

            // Read guid
            if (ret != null && ret.size() > 0) {
                final List<ElementGuid> guidList = getElementGuidList();
                if (guidList != null && guidList.size() > 0) {
                    Map<Long, ElementGuid> elementIdGuidMap = new HashMap<Long, ElementGuid>() {{
                       for (ElementGuid item : guidList) {
                           put(item.getElementId(), item);
                       }
                    }};

                    for (Element item : ret) {
                        ElementGuid guid = elementIdGuidMap.get(item.getId());
                        if (guid != null) {
                            item.setGuid(guid.getGuid());
                        }
                    }
                }
            }
        }
        return ret;
    }

    public static List<ElementGuid> getElementGuidList() {
        List<ElementGuid> ret = null;
        synchronized ("getElementGuidList") {
            ret = DB.getDb().getMapper(ElementGuidMapper.class).selectAll();
        }
        return ret;
    }

    public static Element addElement(Element item) {
        return addElement(item, true);
    }

    public static Element addElement(final Element item, boolean commit) {
        if (item == null) {
            return null;
        }

        int count = DB.getDb().getMapper(ElementMapper.class).insert(item);
        if (count > 0) {
            // Fill element-guid
            addElementGuid(new ElementGuid(){{
                setElementId(item.getId());
                setGuid(item.getGuid());
                setFileId(item.getFile().getId());
            }}, false);

            if (commit) {
                DB.getDb().commit();
            }
        }
        return count <= 0 ? null : item;
    }

    private static ElementGuid addElementGuid(ElementGuid item, boolean commit) {
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

    public static List<User> getUserList() {
        List<User> ret = null;
        synchronized ("getUserList") {
            ret = DB.getDb().getMapper(UserMapper.class).selectAll();
        }
        return ret;
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
