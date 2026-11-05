/*
 * ====================================================================
 *  PART    : M2 - LECTURER
 *  PURPOSE : Add / modify / delete course materials (FR-17).
 *  PLUGS IN: LecturerDashboard -> 'Materials'. Uses Lecturer.addMaterial() and MaterialDAO.
 *  OOP     : Inheritance (extends BasePanel), exception handling, validation.
 * ====================================================================
 */
package lecturer;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MaterialPanel extends BasePanel {
    private final Lecturer lecturer;
    private final TablePanel table = new TablePanel("", "Date", "Course", "Title", "File", "Stored", "From", "To", "Status");
    private final JComboBox<Course> course = new JComboBox<>();
    private final JTextField title = Theme.text(18);
    private final JTextField file = Theme.text(18);
    private final JTextField fromDate = Theme.text(18);   // validity period: yyyy-MM-dd
    private final JTextField toDate = Theme.text(18);
    private List<CourseMaterial> items = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();

    public MaterialPanel(Lecturer lecturer) {
        super("Course Materials", "Share lecture notes, slides and files with your students");
        this.lecturer = lecturer;
        FormPanel f = new FormPanel();
        f.addRow("Course", course);
        f.addRow("Title", title);
        JPanel fileRow = Theme.transparent(new BorderLayout(6, 0));
        fileRow.add(file, BorderLayout.CENTER);
        RoundedButton browse = new RoundedButton("Browse", RoundedButton.Kind.SECONDARY);
        browse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) file.setText(fc.getSelectedFile().getPath());
        });
        fileRow.add(browse, BorderLayout.EAST);
        f.addRow("File", fileRow);
        f.addRow("From Date (yyyy-MM-dd)", fromDate);
        f.addRow("To Date (yyyy-MM-dd)", toDate);
        resetDates();
        RoundedButton add = new RoundedButton("Add");
        RoundedButton upd = new RoundedButton("Update", RoundedButton.Kind.SUCCESS);
        RoundedButton del = new RoundedButton("Delete", RoundedButton.Kind.DANGER);
        add.addActionListener(e -> save(false));
        upd.addActionListener(e -> save(true));
        del.addActionListener(e -> delete());
        f.addButtons(add, upd, del);
        f.finish();
        Card side = new Card(new BorderLayout(0, 8));
        side.add(Theme.heading("Material"), BorderLayout.NORTH);
        side.add(f, BorderLayout.CENTER);
        side.setPreferredSize(new Dimension(370, 100));
        body.add(table, BorderLayout.CENTER);
        body.add(side, BorderLayout.EAST);
        table.onSelect(() -> {
            int i = table.getSelectedModelRow();
            if (i >= 0) {
                CourseMaterial m = items.get(i);
                title.setText(m.getTitle());
                file.setText(m.isHasFile() ? "" : m.getFilePath());   // empty = keep the stored file; Browse = replace it
                fromDate.setText(m.getFromDate() == null ? "" : m.getFromDate().toString());
                toDate.setText(m.getToDate() == null ? "" : m.getToDate().toString());
                for (int k = 0; k < course.getItemCount(); k++) if (course.getItemAt(k).getCourseId() == m.getCourse().getCourseId()) course.setSelectedIndex(k);
            }
        });
    }

    @Override
    public void onShow() {
        try {
            courses = lecturer.myCourses();
            course.removeAllItems();
            for (Course c : courses) course.addItem(c);
            items = lecturer.manageMaterials().findByLecturer(lecturer.getUserId(), courses);
            List<Object[]> rows = new ArrayList<>();
            for (CourseMaterial m : items) rows.add(new Object[]{m.getCreatedDate(), m.getCourse().getCourseCode(), m.getTitle(),
                    m.getFileName().isEmpty() ? m.getFilePath() : m.getFileName(), m.isHasFile() ? "YES" : "NO",
                    m.getFromDate() == null ? "-" : m.getFromDate(), m.getToDate() == null ? "-" : m.getToDate(), m.getStatus()});
            table.setRows(rows);
        } catch (DatabaseException e) {
            Dialogs.handle(this, e);
        }
    }

    private void save(boolean update) {
        try {
            if (course.getSelectedItem() == null) throw new ValidationException("You have no courses yet.");
            if (title.getText().trim().isEmpty()) throw new ValidationException("Title is required.");
            boolean keepStored = update && table.getSelectedModelRow() >= 0 && items.get(table.getSelectedModelRow()).isHasFile();
            if (file.getText().trim().isEmpty() && !keepStored) throw new ValidationException("Please choose the file to upload (Browse).");
            if (!update && !new java.io.File(file.getText().trim()).isFile()) throw new ValidationException("File not found. Use Browse to choose the file students will download.");
            LocalDate from = parseDate(fromDate.getText(), "From Date");
            LocalDate to = parseDate(toDate.getText(), "To Date");
            if (from.isAfter(to)) throw new ValidationException("From Date cannot be after To Date.");
            if (!update && to.isBefore(LocalDate.now())) throw new ValidationException("To Date is already in the past, so students could never see this material.");
            CourseMaterial m;
            if (update) {
                int i = table.getSelectedModelRow();
                if (i < 0) throw new ValidationException("Select a material in the table first.");
                m = items.get(i);
                m.setCourse((Course) course.getSelectedItem());
                m.updateMaterial(title.getText().trim(), file.getText().trim());
                m.setFromDate(from);
                m.setToDate(to);
                lecturer.manageMaterials().update(m);
            } else {
                m = new CourseMaterial();
                m.setCourse((Course) course.getSelectedItem());
                m.updateMaterial(title.getText().trim(), file.getText().trim());
                m.setFromDate(from);
                m.setToDate(to);
                lecturer.addMaterial(m);
            }
            title.setText("");
            file.setText("");
            resetDates();
            onShow();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }

    /** Default validity period for a new material: today until 30 days from now (the lecturer can change it). */
    private void resetDates() {
        fromDate.setText(LocalDate.now().toString());
        toDate.setText(LocalDate.now().plusDays(30).toString());
    }

    private static LocalDate parseDate(String text, String label) throws ValidationException {
        String t = text.trim();
        if (t.isEmpty()) throw new ValidationException(label + " is required. Use the format yyyy-MM-dd, e.g. 2026-11-30.");
        try {
            return LocalDate.parse(t);
        } catch (DateTimeParseException e) {
            throw new ValidationException(label + " is not a valid date. Use the format yyyy-MM-dd, e.g. 2026-11-30.", e);
        }
    }

    private void delete() {
        int i = table.getSelectedModelRow();
        if (i < 0) { Dialogs.error(this, "Select a material in the table first."); return; }
        if (!Dialogs.confirm(this, "Delete this material?")) return;
        try {
            lecturer.manageMaterials().delete(items.get(i).getResourceId());
            onShow();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }
}