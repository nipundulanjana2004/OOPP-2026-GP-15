
package common;

public class AttendanceTally {
    private double totalHours = 0;
    private double presentHours = 0;
    private double excusedHours = 0;

    public AttendanceTally() {
    }

    public AttendanceTally(double totalHours, double presentHours, double excusedHours) {
        this.totalHours = totalHours;
        this.presentHours = presentHours;
        this.excusedHours = excusedHours;
    }

    public double getTotalHours() { return totalHours; }
    public double getPresentHours() { return presentHours; }
    public double getExcusedHours() { return excusedHours; }


    public AttendanceTally plus(AttendanceTally other) {
        return new AttendanceTally(totalHours + other.totalHours, presentHours + other.presentHours,
                excusedHours + other.excusedHours);
    }

    public boolean isEmpty() {
        return totalHours <= 0;
    }
}
