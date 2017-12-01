package jira.tool.db.model;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("Story")
public class Story {
    private long id;
    private String key;
    private String title;
    private String type;
    private String status;
    private String result;
    private String label;
    private String EAGUID;
    private String projectName;
    private String projectKey;
    private long customerId;
    private String customer;
    private Date dueDate;
    private Date resultDate;
    private Date releaseDate;
    private Date startDate;
    private long originalEstimation;
    private long estimation;
    private long timeSpent;

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getResultDate() {
        return resultDate;
    }

    public void setResultDate(Date resultDate) {
        this.resultDate = resultDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public long getOriginalEstimation() {
        return originalEstimation;
    }

    public void setOriginalEstimation(long originalEstimation) {
        this.originalEstimation = originalEstimation;
    }

    public long getEstimation() {
        return estimation;
    }

    public void setEstimation(long estimation) {
        this.estimation = estimation;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getEAGUID() {
        return EAGUID;
    }

    public void setEAGUID(String EAGUID) {
        this.EAGUID = EAGUID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }
}
