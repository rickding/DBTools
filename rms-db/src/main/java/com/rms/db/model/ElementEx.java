package com.rms.db.model;

import java.util.ArrayList;
import java.util.List;

public class ElementEx extends Element {
    private Project project;
    private List<String> guidList = new ArrayList<String>();
    private List<File> fileList = new ArrayList<File>();

    private String projectName;
    private String guid;
    private String fileName;
    private String filePath;

    private String storyKey;
    private String storyName;
    private String storyGuid;
    private String epicGuid;
    private String epicKey;
    private String epicName;

    private String pdName;
    private String devName;
    private String qaName;

    private String typeName;
    private String statusName;
    private Double manDay;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<String> getGuidList() {
        return guidList;
    }

    public void setGuidList(String guid) {
        if (guid != null && guid.length() > 0) {
            this.guidList.add(guid);
        }
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(File file) {
        if (file != null) {
            this.fileList.add(file);
        }
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getStoryKey() {
        return storyKey;
    }

    public void setStoryKey(String storyKey) {
        this.storyKey = storyKey;
    }

    public String getStoryGuid() {
        return storyGuid;
    }

    public void setStoryGuid(String storyGuid) {
        this.storyGuid = storyGuid;
    }

    public String getEpicGuid() {
        return epicGuid;
    }

    public void setEpicGuid(String epicGuid) {
        this.epicGuid = epicGuid;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public String getEpicKey() {
        return epicKey;
    }

    public void setEpicKey(String epicKey) {
        this.epicKey = epicKey;
    }

    public String getEpicName() {
        return epicName;
    }

    public void setEpicName(String epicName) {
        this.epicName = epicName;
    }

    public String getPdName() {
        return pdName;
    }

    public void setPdName(String pdName) {
        this.pdName = pdName;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getQaName() {
        return qaName;
    }

    public void setQaName(String qaName) {
        this.qaName = qaName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Double getManDay() {
        return manDay;
    }

    public void setManDay(Double manDay) {
        this.manDay = manDay;
    }
}