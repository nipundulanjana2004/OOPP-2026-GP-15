/*
 * ====================================================================
 *  PART    : M2 / M4 - UNDERGRADUATE
 *  PURPOSE : Lists the lecture materials of MY registered courses and lets me download the file.
 *  PLUGS IN: UndergraduateDashboard -> 'Materials'. Uses Undergraduate.viewMaterials() / downloadMaterial() -> lecturer.MaterialDAO.
 *  OOP     : INHERITANCE (extends UndergraduateViewPanel), exception handling, encapsulation (student only sees own courses).
 * ====================================================================
 */
package undergraduate;

import common.*;
import common.ui.*;
import lecturer.CourseMaterial;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyMaterialsPanel extends UndergraduateViewPanel {
    private final TablePanel table = new TablePanel("", "Date", "Course", "Title", "File", "Available");
    private List<CourseMaterial> items = new ArrayList<>();

    public MyMaterialsPanel(Undergraduate student) {
        super(student, "Course Materials", "Lecture notes and slides shared by your lecturers - select a row and press Download");
        RoundedButton download = new RoundedButton("Download selected", RoundedButton.Kind.SUCCESS);
        download.addActionListener(e -> download());
        table.addToolbarComponent(download);
        table.setColumnWidth(2, 260);
        body.add(table, BorderLayout.CENTER);
        table.getTable().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) download();     // double click = download
            }
        });
    }

    @Override
    protected void loadData() throws AppException {
        items = student.viewMaterials();
        List<Object[]> rows = new ArrayList<>();
        for (CourseMaterial m : items) {
            rows.add(new Object[]{m.getCreatedDate(), m.getCourse().getCourseCode(), m.getTitle(),
                    m.getFileName().isEmpty() ? "-" : m.getFileName(), m.isHasFile() ? "YES" : "NO"});
        }
        table.setRows(rows);
    }

    private void download() {
        int i = table.getSelectedModelRow();
        if (i < 0) { Dialogs.error(this, "Select a material in the table first."); return; }
        CourseMaterial m = items.get(i);
        if (!m.isHasFile()) { Dialogs.error(this, "The lecturer has not attached a file to this material yet."); return; }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save lecture material");
        fc.setSelectedFile(new File(m.getFileName().isEmpty() ? m.getTitle() : m.getFileName()));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File target = fc.getSelectedFile();
        if (target.exists() && !Dialogs.confirm(this, target.getName() + " already exists. Replace it?")) return;
        try {
            student.downloadMaterial(m, target);
            Dialogs.info(this, "Saved to " + target.getAbsolutePath());
        } catch (AppException ex) {
            Dialogs.handle(this, ex);
        }
    }
}
