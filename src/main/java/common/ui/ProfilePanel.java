/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4) (the editable fields are decided by each member's User subclass)
 *  PURPOSE : My Profile page. Which fields can be edited comes from user.getEditableFields(): Admin all, Lecturer/Technical Officer all except username, Undergraduate ONLY contact details + photo.
 *  PLUGS IN: M1, M2, M3, M4 dashboards each add new ProfilePanel(user).
 *  OOP     : POLYMORPHISM (getEditableFields / updateProfile differ per subclass), encapsulation, validation + exception handling, database update.
 * ====================================================================
 */
package common.ui;

import common.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProfilePanel extends BasePanel {
    private final User user;
    private final JTextField username = Theme.text(20);
    private final JTextField firstName = Theme.text(20);
    private final JTextField lastName = Theme.text(20);
    private final JTextField email = Theme.text(20);
    private final JTextField contact = Theme.text(20);
    private final JTextField address = Theme.text(20);
    private final AvatarLabel avatar;
    private String pendingPicture;

    public ProfilePanel(User user) {
        super("My Profile", "Fields shown in grey are managed by the Admin and cannot be changed by you");
        this.user = user;
        this.pendingPicture = user.getProfilePicture();
        avatar = new AvatarLabel(120, user);

        Card card = new Card(new BorderLayout(30, 0));
        JPanel left = Theme.transparent(new BorderLayout(0, 12));
        left.add(avatar, BorderLayout.NORTH);
        JPanel info = Theme.transparent(new GridLayout(0, 1, 0, 2));
        info.add(Theme.heading(user.getFullName()));
        info.add(Theme.muted(user.getRoleSummary()));
        left.add(info, BorderLayout.CENTER);
        RoundedButton photo = new RoundedButton("Change photo", RoundedButton.Kind.SECONDARY);
        photo.addActionListener(e -> choosePhoto());
        photo.setEnabled(user.getEditableFields().contains("profilePicture"));
        left.add(photo, BorderLayout.SOUTH);
        left.setPreferredSize(new Dimension(220, 300));
        card.add(left, BorderLayout.WEST);

        FormPanel form = new FormPanel();
        form.addRow("Username", username);
        form.addRow("First name", firstName);
        form.addRow("Last name", lastName);
        form.addRow("E-mail", email);
        form.addRow("Contact number", contact);
        form.addRow("Address", address);
        RoundedButton save = new RoundedButton("Save changes");
        save.addActionListener(e -> save());
        form.addButtons(save);
        form.finish();
        card.add(form, BorderLayout.CENTER);

        JPanel wrap = Theme.transparent(new BorderLayout());
        wrap.add(card, BorderLayout.NORTH);
        body.add(wrap, BorderLayout.CENTER);
        load();
    }

    private void load() {
        username.setText(user.getUsername());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        email.setText(user.getEmail());
        contact.setText(user.getContactNumber());
        address.setText(user.getAddress());
        Set<String> ok = user.getEditableFields();
        lock(username, ok.contains("username"));
        lock(firstName, ok.contains("firstName"));
        lock(lastName, ok.contains("lastName"));
        lock(email, ok.contains("email"));
        lock(contact, ok.contains("contactNumber"));
        lock(address, ok.contains("address"));
    }

    private void lock(JTextField f, boolean editable) {
        f.setEditable(editable);
        f.setBackground(editable ? Color.WHITE : new Color(0xEEF1F7));
        f.setForeground(editable ? Theme.TEXT : Theme.MUTED);
    }

    private void choosePhoto() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Images (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            File src = fc.getSelectedFile();
            File dir = new File("profile_pictures");
            if (!dir.exists() && !dir.mkdirs()) throw new ValidationException("Could not create the profile_pictures folder.");
            String name = src.getName();
            String ext = name.contains(".") ? name.substring(name.lastIndexOf('.')) : ".png";
            File dest = new File(dir, user.getUsername() + "_" + System.currentTimeMillis() + ext);
            Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            pendingPicture = dest.getPath();
            avatar.setPicture(pendingPicture);
        } catch (Exception e) {
            Dialogs.handle(this, e instanceof AppException ? e : new ValidationException("Could not use that picture."));
        }
    }

    private void validate(Map<String, String> d) throws ValidationException {
        if (d.get("firstName").isEmpty() || d.get("lastName").isEmpty()) {
            throw new ValidationException("First name and last name are required.");
        }
        String mail = d.get("email");
        if (!mail.isEmpty() && !mail.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new ValidationException("Please enter a valid e-mail address.");
        }
        String tel = d.get("contactNumber");
        if (!tel.isEmpty() && !tel.matches("^\\+?[0-9 ]{9,15}$")) {
            throw new ValidationException("Contact number must be 9-15 digits (a leading + is allowed).");
        }
    }

    private void save() {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("username", username.getText().trim());
            data.put("firstName", firstName.getText().trim());
            data.put("lastName", lastName.getText().trim());
            data.put("email", email.getText().trim());
            data.put("contactNumber", contact.getText().trim());
            data.put("address", address.getText().trim());
            data.put("profilePicture", pendingPicture == null ? "" : pendingPicture);
            validate(data);
            user.updateProfile(data);            // polymorphic: each role applies only what it may change
            new UserDAO().update(user);
            Dialogs.info(this, "Your profile was updated.");
            load();
        } catch (Exception e) {
            Dialogs.handle(this, e);
        }
    }
}
