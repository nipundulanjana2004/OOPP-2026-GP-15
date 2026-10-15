package common;

import java.sql.Date;

public abstract class AcademicResource {
    private int resourceId = 0;
    private String title = "";
    private Date createdDate = new Date(System.currentTimeMillis());

    public AcademicResource() {
    }

    public AcademicResource(int resourceId, String title, Date createdDate) {
        this.resourceId = resourceId;
        this.title = title;
        this.createdDate = createdDate;
    }

    public int getResourceId() { return resourceId; }
    public void setResourceId(int resourceId) { this.resourceId = resourceId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }


    public abstract String display();
}
