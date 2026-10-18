
package common;

public enum Role {
    ADMIN("Admin", "admin.Admin", "admin.AdminDashboard"),                                        // M1
    LECTURER("Lecturer", "lecturer.Lecturer", "lecturer.LecturerDashboard"),                       // M2
    TECH_OFFICER("Technical Officer", "officer.TechnicalOfficer", "officer.OfficerDashboard"),     // M3
    UNDERGRADUATE("Undergraduate", "undergraduate.Undergraduate", "undergraduate.UndergraduateDashboard"); // M4

    private final String label;
    private final String modelClass;
    private final String dashboardClass;

    Role(String label, String modelClass, String dashboardClass) {
        this.label = label;
        this.modelClass = modelClass;
        this.dashboardClass = dashboardClass;
    }

    public String getLabel() {
        return label;
    }

    public String getModelClass() {
        return modelClass;
    }

    public String getDashboardClass() {
        return dashboardClass;
    }

    @Override
    public String toString() {
        return label;
    }
}
