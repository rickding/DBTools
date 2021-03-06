package com.rms.db.model;

import java.util.Date;

public class JiraIssue {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column jira_issue.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column jira_issue.issue_key
     *
     * @mbg.generated
     */
    private String issueKey;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column jira_issue.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column jira_issue.guid
     *
     * @mbg.generated
     */
    private String guid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column jira_issue.is_deleted
     *
     * @mbg.generated
     */
    private Boolean isDeleted;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column jira_issue.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column jira_issue.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column jira_issue.id
     *
     * @return the value of jira_issue.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column jira_issue.id
     *
     * @param id the value for jira_issue.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column jira_issue.issue_key
     *
     * @return the value of jira_issue.issue_key
     *
     * @mbg.generated
     */
    public String getIssueKey() {
        return issueKey;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column jira_issue.issue_key
     *
     * @param issueKey the value for jira_issue.issue_key
     *
     * @mbg.generated
     */
    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey == null ? null : issueKey.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column jira_issue.name
     *
     * @return the value of jira_issue.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column jira_issue.name
     *
     * @param name the value for jira_issue.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column jira_issue.guid
     *
     * @return the value of jira_issue.guid
     *
     * @mbg.generated
     */
    public String getGuid() {
        return guid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column jira_issue.guid
     *
     * @param guid the value for jira_issue.guid
     *
     * @mbg.generated
     */
    public void setGuid(String guid) {
        this.guid = guid == null ? null : guid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column jira_issue.is_deleted
     *
     * @return the value of jira_issue.is_deleted
     *
     * @mbg.generated
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column jira_issue.is_deleted
     *
     * @param isDeleted the value for jira_issue.is_deleted
     *
     * @mbg.generated
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column jira_issue.create_time
     *
     * @return the value of jira_issue.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column jira_issue.create_time
     *
     * @param createTime the value for jira_issue.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column jira_issue.update_time
     *
     * @return the value of jira_issue.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column jira_issue.update_time
     *
     * @param updateTime the value for jira_issue.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}