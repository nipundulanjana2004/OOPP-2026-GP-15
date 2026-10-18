
package common;

import java.util.ArrayList;
import java.util.List;

public class GradeCalculator {
    private static List<GradeScale> scale = null;

    private GradeCalculator() {
    }

    public static void reloadScale() {
        scale = null;
    }

    private static List<GradeScale> getScale() {
        if (scale == null) {
            try {
                scale = new GradeScaleDAO().findAll();
            } catch (DatabaseException e) {
                scale = new ArrayList<>();
            }
        }
        return scale;
    }

    public static List<GradeScale> getAllGrades() {
        return getScale();
    }

    public static GradeScale assignGrade(double marks) {
        for (GradeScale g : getScale()) {
            if (g.contains(marks)) {
                return g;
            }
        }
        return null;
    }

    public static double calculateGPA(List<CourseResult> results) {
        double points = 0;
        double credits = 0;
        for (CourseResult r : results) {
            double gp = r.gradePointOrMinusOne();
            if (gp >= 0) {
                points += gp * r.getCourse().getTotalCredits();
                credits += r.getCourse().getTotalCredits();
            }
        }
        return credits == 0 ? 0 : points / credits;
    }

    public static double countedCredits(List<CourseResult> results) {
        double credits = 0;
        for (CourseResult r : results) {
            if (r.gradePointOrMinusOne() >= 0) {
                credits += r.getCourse().getTotalCredits();
            }
        }
        return credits;
    }
}
