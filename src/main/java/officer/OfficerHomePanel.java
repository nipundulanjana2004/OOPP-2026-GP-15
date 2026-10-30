/*
 * ====================================================================
 *  PART    : M3 - TECHNICAL OFFICER
 *  PURPOSE : Overview: pending medicals waiting for a decision + quick numbers for the officer's department.
 *  PLUGS IN: First page of OfficerDashboard.
 *  OOP     : Inheritance (extends BasePanel).
 * ====================================================================
 */
package officer;

import common.*;
import common.ui.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OfficerHomePanel extends BasePanel {
    private final TechnicalOfficer officer;
    private final java.awt.Container cards = Theme.transparent(new GridLayout(1, 3, 16, 0));
    private final TablePanel pending = new TablePanel("Medicals waiting for approval", "Reg. No", "Name", "From", "To", "Reason", "Status");

    public OfficerHomePanel(TechnicalOfficer officer) {
        super("Welcome, " + officer.getFirstName(), officer.getRoleSummary());
        this.officer = officer;
        body.add(cards, BorderLayout.NORTH);
        body.add(pending, BorderLayout.CENTER);
    }

    private Card stat(String label, int value, Color color) {
        Card c = new Card(new BorderLayout(0, 4));
        javax.swing.JLabel n = new javax.swing.JLabel(String.valueOf(value));
        n.setFont(Theme.BIG_NUMBER);
        n.setForeground(color);
        c.add(n, BorderLayout.CENTER);
        c.add(Theme.muted(label), BorderLayout.SOUTH);
        c.setPreferredSize(new Dimension(200, 100));
        return c;
    }

    @Override
    public void onShow() {
        try {
            List<Medical> all = officer.medicals().findByDepartment(officer.getDepartmentId());
            List<Object[]> rows = new ArrayList<>();
            for (Medical m : all) {
                if (m.getApprovalStatus() == ApprovalStatus.PENDING) {
                    rows.add(new Object[]{m.getStudent().getRegistrationNumber(), m.getStudent().getFullName(), m.getDateFrom(), m.getDateTo(), m.getReason(), "PENDING"});
                }
            }
            pending.setRows(rows);
            ((javax.swing.JComponent) cards).removeAll();
            cards.add(stat("Pending medicals", rows.size(), Theme.WARNING));
            cards.add(stat("Medicals on record", all.size(), Theme.ACCENT));
            cards.add(stat("Courses in my department", officer.departmentCourses().size(), Theme.SUCCESS));
            ((javax.swing.JComponent) cards).revalidate();
            ((javax.swing.JComponent) cards).repaint();
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }
}
