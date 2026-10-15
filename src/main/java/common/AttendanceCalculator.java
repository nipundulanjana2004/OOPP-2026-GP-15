
package common;

public class AttendanceCalculator {
    public static final double MINIMUM_PERCENT = 80.0;

    private AttendanceCalculator() {
    }


    public static double rawPercent(AttendanceTally t) {
        return t.isEmpty() ? 0 : t.getPresentHours() * 100.0 / t.getTotalHours();
    }

    public static double effectivePercent(AttendanceTally t) {
        return t.isEmpty() ? 0 : (t.getPresentHours() + t.getExcusedHours()) * 100.0 / t.getTotalHours();
    }

    public static double theoryPercent(AttendanceTally theory) {
        return effectivePercent(theory);
    }

    public static double practicalPercent(AttendanceTally practical) {
        return effectivePercent(practical);
    }

    public static double combinedPercent(AttendanceTally theory, AttendanceTally practical) {
        return effectivePercent(theory.plus(practical));
    }


    public static boolean isEligible(double effectivePercent, boolean anySessions) {
        return !anySessions || effectivePercent + 1e-9 >= MINIMUM_PERCENT;
    }

    public static String scenario(double raw, double effective, boolean hasMedical) {
        boolean exact = Math.abs(effective - 80.0) < 1e-9;
        if (exact) return "Exactly 80%";
        if (effective > 80) return hasMedical && raw < 80 ? ">80% with medicals" : ">80%";
        return hasMedical ? "<80% even with medicals" : "<80% without medicals";
    }
}
