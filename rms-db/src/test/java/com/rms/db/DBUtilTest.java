package com.rms.db;

import com.rms.db.model.ElementEx;
import com.rms.db.model.ElementGuid;
import com.rms.db.model.File;
import com.rms.db.model.JiraIssue;
import com.rms.db.model.Project;
import com.rms.db.model.Status;
import com.rms.db.model.Type;
import com.rms.db.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DBUtilTest {
    @Test
    public void testGetElementList() {
        List<ElementEx> ret = DBUtil.getElementList();
        System.out.printf("Elements from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetElementGuidList() {
        List<ElementGuid> ret = DBUtil.getElementGuidList();
        System.out.printf("ElementGuids from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetFileList() {
        List<File> ret = DBUtil.getFileList();
        System.out.printf("Files from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetJiraIssueList() {
        List<JiraIssue> ret = DBUtil.getJiraIssueList();
        System.out.printf("Jira issues from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetProjectList() {
        List<Project> ret = DBUtil.getProjectList();
        System.out.printf("Projects from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetStatusList() {
        List<Status> ret = DBUtil.getStatusList();
        System.out.printf("Statuses from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetTypeList() {
        List<Type> ret = DBUtil.getTypeList();
        System.out.printf("Types from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetUserList() {
        List<User> ret = DBUtil.getUserList();
        System.out.printf("Users from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }
}
