package com.rms.db.model;

import java.util.Date;

public class Project {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column project.id
     *
     * @mbg.generated
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column project.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column project.aliases
     *
     * @mbg.generated
     */
    private String aliases;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column project.sub_project
     *
     * @mbg.generated
     */
    private String subProject;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column project.is_deleted
     *
     * @mbg.generated
     */
    private Boolean isDeleted;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column project.create_time
     *
     * @mbg.generated
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column project.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column project.id
     *
     * @return the value of project.id
     *
     * @mbg.generated
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column project.id
     *
     * @param id the value for project.id
     *
     * @mbg.generated
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column project.name
     *
     * @return the value of project.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column project.name
     *
     * @param name the value for project.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column project.aliases
     *
     * @return the value of project.aliases
     *
     * @mbg.generated
     */
    public String getAliases() {
        return aliases;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column project.aliases
     *
     * @param aliases the value for project.aliases
     *
     * @mbg.generated
     */
    public void setAliases(String aliases) {
        this.aliases = aliases == null ? null : aliases.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column project.sub_project
     *
     * @return the value of project.sub_project
     *
     * @mbg.generated
     */
    public String getSubProject() {
        return subProject;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column project.sub_project
     *
     * @param subProject the value for project.sub_project
     *
     * @mbg.generated
     */
    public void setSubProject(String subProject) {
        this.subProject = subProject == null ? null : subProject.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column project.is_deleted
     *
     * @return the value of project.is_deleted
     *
     * @mbg.generated
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column project.is_deleted
     *
     * @param isDeleted the value for project.is_deleted
     *
     * @mbg.generated
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column project.create_time
     *
     * @return the value of project.create_time
     *
     * @mbg.generated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column project.create_time
     *
     * @param createTime the value for project.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column project.update_time
     *
     * @return the value of project.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column project.update_time
     *
     * @param updateTime the value for project.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}