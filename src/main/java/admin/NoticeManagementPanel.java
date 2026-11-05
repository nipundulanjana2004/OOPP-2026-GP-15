/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Post / edit / delete notices and choose who sees them: everyone, a role, a department or a batch (FR-11).
 *  PLUGS IN: AdminDashboard -> 'Notices'. M2, M3, M4 read them through common.NoticePanel.
 *  OOP     : INHERITANCE (extends ManagementPanel<Notice>); uses Notice (extends AcademicResource) and the NoticeTarget enum.
 * ====================================================================
 */
package admin;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.util.*;

public class NoticeManagementPanel extends ManagementPanel<Notice> {
    private final Admin admin;
    private final JTextField title = Theme.text(18);
    private final JTextArea content = Theme.area(6, 18);
    private final JComboBox<NoticeTarget> target = Theme.combo(NoticeTarget.values());
    private final JComboBox<Role> role = Theme.combo(Role.values());
    private final JComboBox<Department> department = new JComboBox<>();
    private final JComboBox<Batch> batch = new JComboBox<>();
    private final JTextField fromDate = Theme.text(18);
    private final JTextField toDate = Theme.text(18);

    public NoticeManagementPanel(Admin admin) {
        super("Notices", "Publish announcements for everyone or a specific audience", "Notice details",
                "Date", "Title", "Audience", "Visible from", "Visible to", "Posted by");
        this.admin = admin;
        target.addActionListener(e -> updateTargets());
        init();
    }

    @Override
    protected void buildForm(FormPanel f) {
        f.addRow("Title", title);
        f.addRow("Content", new JScrollPane(content));
        f.addRow("Audience", target);
        f.addRow("Role (if audience = ROLE)", role);
        f.addRow("Department (if audience = DEPARTMENT)", department);
        f.addRow("Batch (if audience = BATCH)", batch);
        f.addRow("Show from (yyyy-MM-dd, blank = immediately)", fromDate);
        f.addRow("Show until (yyyy-MM-dd, blank = never expires)", toDate);
        updateTargets();
    }

    private void updateTargets() {
        NoticeTarget t = (NoticeTarget) target.getSelectedItem();
        role.setEnabled(t == NoticeTarget.ROLE);
        department.setEnabled(t == NoticeTarget.DEPARTMENT);
        batch.setEnabled(t == NoticeTarget.BATCH);
    }

    @Override
    protected java.util.List<Notice> loadItems() throws AppException {
        LookupDAO lookup = new LookupDAO();
        department.removeAllItems();
        for (Department d : lookup.departments()) department.addItem(d);
        batch.removeAllItems();
        for (Batch b : lookup.batches()) batch.addItem(b);
        return admin.manageNotices().findAll();
    }

    @Override
    protected Object[] toRow(Notice n) {
        return new Object[]{n.getCreatedDate(), n.getTitle(), n.audience(),
                n.getFromDate() == null ? "-" : n.getFromDate(),
                n.getToDate() == null ? "-" : n.getToDate(),
                n.getPostedByName()};
    }

    @Override
    protected void fillForm(Notice n) {
        title.setText(n.getTitle());
        content.setText(n.getContent());
        target.setSelectedItem(n.getTargetType());
        if (n.getTargetRole() != null) role.setSelectedItem(n.getTargetRole());
        if (n.getTargetDepartment() != null) {
            for (int i = 0; i < department.getItemCount(); i++)
                if (department.getItemAt(i).getDepartmentId() == n.getTargetDepartment().getDepartmentId()) department.setSelectedIndex(i);
        }
        if (n.getTargetBatch() != null) {
            for (int i = 0; i < batch.getItemCount(); i++)
                if (batch.getItemAt(i).getBatchId() == n.getTargetBatch().getBatchId()) batch.setSelectedIndex(i);
        }
        fromDate.setText(n.getFromDate() == null ? "" : n.getFromDate().toString());
        toDate.setText(n.getToDate() == null ? "" : n.getToDate().toString());
        updateTargets();
    }

    @Override
    protected void clearForm() {
        title.setText("");
        content.setText("");
        fromDate.setText("");
        toDate.setText("");
        target.setSelectedItem(NoticeTarget.ALL);
        updateTargets();
    }

    private java.sql.Date parseDate(String text, String label) throws ValidationException {
        String s = text == null ? "" : text.trim();
        if (s.isEmpty()) return null;
        try {
            return java.sql.Date.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(label + " must be a date in the format yyyy-MM-dd (e.g. 2026-11-20).");
        }
    }

    private Notice readForm(Notice n) throws AppException {
        n.setTitle(need(title.getText(), "Title"));
        n.setContent(need(content.getText(), "Content"));
        NoticeTarget t = (NoticeTarget) target.getSelectedItem();
        n.setTargetType(t);
        n.setTargetRole(t == NoticeTarget.ROLE ? (Role) role.getSelectedItem() : null);
        n.setTargetDepartment(t == NoticeTarget.DEPARTMENT ? (Department) department.getSelectedItem() : null);
        n.setTargetBatch(t == NoticeTarget.BATCH ? (Batch) batch.getSelectedItem() : null);
        if (t == NoticeTarget.DEPARTMENT && n.getTargetDepartment() == null) throw new ValidationException("Choose a department.");
        if (t == NoticeTarget.BATCH && n.getTargetBatch() == null) throw new ValidationException("Choose a batch.");
        n.setFromDate(parseDate(fromDate.getText(), "Show from"));
        n.setToDate(parseDate(toDate.getText(), "Show until"));
        if (n.getFromDate() != null && n.getToDate() != null && n.getToDate().before(n.getFromDate()))
            throw new ValidationException("'Show until' cannot be before 'Show from'.");
        return n;
    }

    @Override
    protected void doAdd() throws AppException {
        Notice n = readForm(new Notice());
        n.publish();
        admin.manageNotices().insert(n, admin.getUserId());
    }

    @Override
    protected void doUpdate(Notice n) throws AppException {
        admin.manageNotices().update(readForm(n));
    }

    @Override
    protected void doDelete(Notice n) throws AppException {
        admin.manageNotices().delete(n.getResourceId());
    }
}
