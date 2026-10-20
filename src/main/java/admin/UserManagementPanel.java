/*
 * ====================================================================
 *  PART    : M1 - ADMIN
 *  PURPOSE : Create / update / activate-deactivate / delete / reset password for ALL user profiles (FR-04, FR-07, FR-08).
 *  PLUGS IN: AdminDashboard -> 'Users'. Builds the right User subclass (Lecturer, TechnicalOfficer, Undergraduate ...) with UserFactory - works with the other members' classes without importing them.
 *  OOP     : INHERITANCE (extends ManagementPanel<User>), POLYMORPHISM (setRoleInput / getRoleInput per subclass), validation + exceptions.
 * ====================================================================
 */
package admin;

import common.*;
import common.ui.*;

import javax.swing.*;
import java.util.*;

public class UserManagementPanel extends ManagementPanel<User> {
    private final Admin admin;
    private final JComboBox<Role> role = Theme.combo(Role.values());
    private final JTextField username = Theme.text(18);
    private final JPasswordField password = Theme.password(18);
    private final JTextField first = Theme.text(18);
    private final JTextField last = Theme.text(18);
    private final JTextField email = Theme.text(18);
    private final JTextField contact = Theme.text(18);
    private final JTextField address = Theme.text(18);
    private final JComboBox<Department> department = new JComboBox<>();
    private final JComboBox<Batch> batch = new JComboBox<>();
    private final JTextField designation = Theme.text(18);
    private final JTextField regNo = Theme.text(18);
    private final JComboBox<AttemptType> status = Theme.combo(AttemptType.values());
    private final JCheckBox active = new JCheckBox("Account is active", true);
    private final RoundedButton resetPw = new RoundedButton("Reset password", RoundedButton.Kind.SECONDARY);
    private final RoundedButton toggle = new RoundedButton("Activate / Deactivate", RoundedButton.Kind.SECONDARY);
    private final LookupDAO lookup = new LookupDAO();

    public UserManagementPanel(Admin admin) {
        super("User Profiles", "Create and maintain every Admin, Lecturer, Technical Officer and Undergraduate",
                "User details", "Username", "Name", "Role", "Department", "Reg. No / Designation", "Status");
        this.admin = admin;
        resetPw.addActionListener(e -> run(this::resetPassword));
        toggle.addActionListener(e -> run(this::toggleActive));
        role.addActionListener(e -> updateRoleFields());
        init();
    }

    @Override
    protected void buildForm(FormPanel f) {
        f.addRow("Role", role);
        f.addRow("Username", username);
        f.addRow("Password (new users)", password);
        f.addRow("First name", first);
        f.addRow("Last name", last);
        f.addRow("E-mail", email);
        f.addRow("Contact number", contact);
        f.addRow("Address", address);
        f.addRow("Department", department);
        f.addRow("Batch (undergraduates)", batch);
        f.addRow("Designation (lecturers)", designation);
        f.addRow("Registration No (undergraduates)", regNo);
        f.addRow("Academic status", status);
        f.addRow("", active);
        f.addButtons(resetPw, toggle);
        updateRoleFields();
    }

    private void updateRoleFields() {
        Role r = (Role) role.getSelectedItem();
        department.setEnabled(r != Role.ADMIN);
        batch.setEnabled(r == Role.UNDERGRADUATE);
        regNo.setEnabled(r == Role.UNDERGRADUATE);
        status.setEnabled(r == Role.UNDERGRADUATE);
        designation.setEnabled(r == Role.LECTURER);
    }

    @Override
    protected List<User> loadItems() throws AppException {
        department.removeAllItems();
        for (Department d : lookup.departments()) department.addItem(d);
        batch.removeAllItems();
        for (Batch b : lookup.batches()) batch.addItem(b);
        return admin.manageUsers().findAll();
    }

    @Override
    protected Object[] toRow(User u) {
        Map<String, Object> in = u.getRoleInput();
        Object dept = in.get("department") == null ? "-" : ((Department) in.get("department")).getDepartmentCode();
        String extra = u.getRole() == Role.UNDERGRADUATE ? String.valueOf(in.get("registrationNumber"))
                : u.getRole() == Role.LECTURER ? String.valueOf(in.get("designation")) : "-";
        return new Object[]{u.getUsername(), u.getFullName(), u.getRole().getLabel(), dept, extra, u.isActive() ? "ACTIVE" : "INACTIVE"};
    }

    @Override
    protected void fillForm(User u) {
        role.setSelectedItem(u.getRole());
        role.setEnabled(false);                 // the role of an existing user cannot change
        username.setText(u.getUsername());
        password.setText("");
        first.setText(u.getFirstName());
        last.setText(u.getLastName());
        email.setText(u.getEmail());
        contact.setText(u.getContactNumber());
        address.setText(u.getAddress());
        active.setSelected(u.isActive());
        Map<String, Object> in = u.getRoleInput();
        if (in.get("department") != null) selectById(department, ((Department) in.get("department")).getDepartmentId());
        if (in.get("batch") != null) selectById(batch, ((Batch) in.get("batch")).getBatchId());
        designation.setText(in.get("designation") == null ? "" : in.get("designation").toString());
        regNo.setText(in.get("registrationNumber") == null ? "" : in.get("registrationNumber").toString());
        if (in.get("academicStatus") != null) status.setSelectedItem(in.get("academicStatus"));
        updateRoleFields();
    }

    private <X> void selectById(JComboBox<X> box, int id) {
        for (int i = 0; i < box.getItemCount(); i++) {
            X x = box.getItemAt(i);
            int xid = x instanceof Department ? ((Department) x).getDepartmentId() : ((Batch) x).getBatchId();
            if (xid == id) { box.setSelectedIndex(i); return; }
        }
    }

    @Override
    protected void clearForm() {
        role.setEnabled(true);
        username.setText("");
        password.setText("");
        first.setText("");
        last.setText("");
        email.setText("");
        contact.setText("");
        address.setText("");
        designation.setText("");
        regNo.setText("");
        active.setSelected(true);
        updateRoleFields();
    }

    /** Copies the form into a User object (works for every role through polymorphism). */
    private void readForm(User u) throws AppException {
        u.setUsername(need(username.getText(), "Username"));
        u.setFirstName(need(first.getText(), "First name"));
        u.setLastName(need(last.getText(), "Last name"));
        String mail = email.getText().trim();
        if (!mail.isEmpty() && !mail.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) throw new ValidationException("Please enter a valid e-mail address.");
        String tel = contact.getText().trim();
        if (!tel.isEmpty() && !tel.matches("^\\+?[0-9 ]{9,15}$")) throw new ValidationException("Contact number must be 9-15 digits.");
        u.setEmail(mail);
        u.setContactNumber(tel);
        u.setAddress(address.getText().trim());
        u.setActive(active.isSelected());
        Role r = u.getRole();
        Map<String, Object> in = new HashMap<>();
        if (r != Role.ADMIN) {
            if (department.getSelectedItem() == null) throw new ValidationException("Please choose a department.");
            in.put("department", department.getSelectedItem());
        }
        if (r == Role.LECTURER) in.put("designation", designation.getText().trim());
        if (r == Role.UNDERGRADUATE) {
            if (batch.getSelectedItem() == null) throw new ValidationException("Please choose a batch.");
            in.put("batch", batch.getSelectedItem());
            in.put("registrationNumber", need(regNo.getText(), "Registration number"));
            in.put("academicStatus", status.getSelectedItem());
        }
        u.setRoleInput(in);
    }

    @Override
    protected void doAdd() throws AppException {
        String pw = new String(password.getPassword());
        if (pw.length() < 6) throw new ValidationException("Please give the new user a password of at least 6 characters.");
        User u = UserFactory.create((Role) role.getSelectedItem());
        readForm(u);
        admin.createUser(u, pw);
        Dialogs.info(this, "User '" + u.getUsername() + "' was created.");
    }

    @Override
    protected void doUpdate(User u) throws AppException {
        readForm(u);
        admin.updateUser(u);
        Dialogs.info(this, "User updated.");
    }

    @Override
    protected void doDelete(User u) throws AppException {
        admin.deleteUser(u.getUserId());
    }

    private void resetPassword() throws AppException {
        if (selected == null) throw new ValidationException("Select a user first.");
        String pw = JOptionPane.showInputDialog(this, "New password for " + selected.getUsername() + " (min 6 characters):");
        if (pw == null) return;
        admin.resetPassword(selected.getUserId(), pw);
        Dialogs.info(this, "Password changed.");
    }

    private void toggleActive() throws AppException {
        if (selected == null) throw new ValidationException("Select a user first.");
        admin.deactivateUser(selected.getUserId(), !selected.isActive());
        reload();
    }
}
