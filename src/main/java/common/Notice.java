
package common;

public class Notice extends AcademicResource {
    private String content = "";
    private NoticeTarget targetType = NoticeTarget.ALL;
    private Role targetRole = null;
    private Department targetDepartment = null;
    private Batch targetBatch = null;
    private int postedBy = 0;
    private String postedByName = "";

    public Notice() {
    }

    public Notice(int id, String title, String content, java.sql.Date date) {
        super(id, title, date);
        this.content = content;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public NoticeTarget getTargetType() { return targetType; }
    public void setTargetType(NoticeTarget targetType) { this.targetType = targetType; }
    public Role getTargetRole() { return targetRole; }
    public void setTargetRole(Role targetRole) { this.targetRole = targetRole; }
    public Department getTargetDepartment() { return targetDepartment; }
    public void setTargetDepartment(Department targetDepartment) { this.targetDepartment = targetDepartment; }
    public Batch getTargetBatch() { return targetBatch; }
    public void setTargetBatch(Batch targetBatch) { this.targetBatch = targetBatch; }
    public int getPostedBy() { return postedBy; }
    public void setPostedBy(int postedBy) { this.postedBy = postedBy; }
    public String getPostedByName() { return postedByName; }
    public void setPostedByName(String postedByName) { this.postedByName = postedByName; }

    public String audience() {
        switch (targetType) {
            case ROLE: return "Role: " + (targetRole == null ? "?" : targetRole.getLabel());
            case DEPARTMENT: return "Dept: " + (targetDepartment == null ? "?" : targetDepartment.getDepartmentCode());
            case BATCH: return "Batch: " + (targetBatch == null ? "?" : targetBatch.getBatchName());
            default: return "Everyone";
        }
    }

    public void publish() {
        setCreatedDate(new java.sql.Date(System.currentTimeMillis()));
    }

    @Override
    public String display() {
        return "[" + getCreatedDate() + "] " + getTitle() + "  (" + audience() + ")";
    }
}
