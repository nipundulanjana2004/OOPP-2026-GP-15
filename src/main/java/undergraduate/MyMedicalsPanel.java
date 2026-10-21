
package undergraduate;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyMedicalsPanel extends UndergraduateViewPanel {
    private final TablePanel table = new TablePanel("", "From", "To", "Reason", "Status", "Verified by");
    private final JLabel summary = Theme.muted("");

    public MyMedicalsPanel(Undergraduate student) {
        super(student, "My Medicals", "Submit certificates to your Technical Officer. Approved medicals count towards attendance.");
        body.add(summary, BorderLayout.NORTH);
        body.add(table, BorderLayout.CENTER);
    }

    @Override
    protected void loadData() throws AppException {
        List<Object[]> rows = new ArrayList<>();
        int ok = 0, pending = 0, rejected = 0;
        for (MedicalInfo m : student.viewMedicals()) {
            rows.add(new Object[]{m.getDateFrom(), m.getDateTo(), m.getReason(), m.getStatus(), m.getVerifiedBy()});
            if ("APPROVED".equals(m.getStatus())) ok++; else if ("PENDING".equals(m.getStatus())) pending++; else rejected++;
        }
        summary.setText("Approved: " + ok + "     Pending: " + pending + "     Rejected: " + rejected);
        table.setRows(rows);
    }
}
