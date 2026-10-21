/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : One part of a course's evaluation (e.g. Quiz 1 = CA 10%, Final Exam = FINAL 60%). Marks are always entered out of 100.
 *  PLUGS IN: EvaluationDAO loads them; MarkEntryPanel lets the lecturer enter marks per component; common.ReportService uses the weightages.
 *  OOP     : Encapsulation.
 * ====================================================================
 */
package lecturer;

import common.ComponentType;

public class EvaluationComponent {
    private int componentId = 0;
    private int courseId = 0;
    private String componentName = "";
    private ComponentType componentType = ComponentType.CA;
    private double maxMarks = 100;
    private double weightage = 0;

    public EvaluationComponent() {
    }

    public EvaluationComponent(int componentId, int courseId, String componentName, ComponentType componentType, double weightage) {
        this.componentId = componentId;
        this.courseId = courseId;
        this.componentName = componentName;
        this.componentType = componentType;
        this.weightage = weightage;
    }

    public int getComponentId() { return componentId; }
    public int getCourseId() { return courseId; }
    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }
    public ComponentType getComponentType() { return componentType; }
    public void setComponentType(ComponentType componentType) { this.componentType = componentType; }
    public double getMaxMarks() { return maxMarks; }
    public double getWeightage() { return weightage; }
    public void setWeightage(double weightage) { this.weightage = weightage; }

    @Override
    public String toString() {
        return componentName + " (" + componentType + ", " + (weightage == Math.rint(weightage) ? String.valueOf((int) weightage) : String.valueOf(weightage)) + "%)";
    }
}
