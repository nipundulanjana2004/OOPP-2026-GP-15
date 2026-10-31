
package admin;

import common.AppException;
import common.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ManagementPanel<T> extends BasePanel {
    protected final TablePanel table;
    protected final FormPanel form = new FormPanel();
    protected List<T> items = new ArrayList<>();
    protected T selected = null;
    private final String formTitle;
    protected final RoundedButton addButton = new RoundedButton("Add");
    protected final RoundedButton updateButton = new RoundedButton("Update", RoundedButton.Kind.SUCCESS);
    protected final RoundedButton deleteButton = new RoundedButton("Delete", RoundedButton.Kind.DANGER);
    protected final RoundedButton clearButton = new RoundedButton("Clear", RoundedButton.Kind.SECONDARY);

    protected interface Action {
        void run() throws AppException;
    }

    protected ManagementPanel(String title, String subtitle, String formTitle, String... columns) {
        super(title, subtitle);
        this.formTitle = formTitle;
        this.table = new TablePanel("", columns);
    }

    protected abstract List<T> loadItems() throws AppException;
    protected abstract Object[] toRow(T item);
    protected abstract void buildForm(FormPanel form);
    protected abstract void fillForm(T item);
    protected abstract void clearForm();
    protected abstract void doAdd() throws AppException;
    protected abstract void doUpdate(T item) throws AppException;
    protected abstract void doDelete(T item) throws AppException;

    protected JComponent createExtras() {
        return null;
    }

    protected void onSelectionChanged(T item) {
    }

    protected final void init() {
        buildForm(form);
        form.addButtons(addButton, updateButton);
        form.addButtons(deleteButton, clearButton);
        form.finish();

        Card side = new Card(new BorderLayout(0, 8));
        side.add(Theme.heading(formTitle), BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(14);
        side.add(sp, BorderLayout.CENTER);
        side.setPreferredSize(new Dimension(370, 100));

        JPanel left = Theme.transparent(new BorderLayout(0, 12));
        left.add(table, BorderLayout.CENTER);
        JComponent extras = createExtras();
        if (extras != null) left.add(extras, BorderLayout.SOUTH);
        body.add(left, BorderLayout.CENTER);
        body.add(side, BorderLayout.EAST);

        addButton.addActionListener(e -> run(() -> { doAdd(); reload(); clearSelection(); }));
        updateButton.addActionListener(e -> run(() -> {
            if (selected == null) throw new common.ValidationException("Select a row in the table first.");
            doUpdate(selected);
            reload();
        }));
        deleteButton.addActionListener(e -> {
            if (selected == null) { Dialogs.error(this, "Select a row in the table first."); return; }
            if (Dialogs.confirm(this, "Delete the selected record? This cannot be undone.")) {
                run(() -> { doDelete(selected); reload(); clearSelection(); });
            }
        });
        clearButton.addActionListener(e -> clearSelection());
        table.onSelect(() -> {
            int i = table.getSelectedModelRow();
            if (i >= 0 && i < items.size()) {
                selected = items.get(i);
                fillForm(selected);
                onSelectionChanged(selected);
                updateButtons();
            }
        });
        updateButtons();
    }

    private void updateButtons() {
        updateButton.setEnabled(selected != null);
        deleteButton.setEnabled(selected != null);
    }

    protected void clearSelection() {
        selected = null;
        table.getTable().clearSelection();
        clearForm();
        onSelectionChanged(null);
        updateButtons();
    }

    protected void run(Action a) {
        try {
            a.run();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        } catch (RuntimeException e) {
            Dialogs.handle(this, e);
        }
        updateButtons();
    }

    protected void reload() throws AppException {
        items = loadItems();
        List<Object[]> rows = new ArrayList<>();
        for (T t : items) rows.add(toRow(t));
        table.setRows(rows);
        selected = null;
    }

    @Override
    public void onShow() {
        run(this::reload);
        updateButtons();
    }

    protected static String need(String value, String fieldName) throws common.ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new common.ValidationException(fieldName + " is required.");
        }
        return value.trim();
    }

    protected static int intIn(JSpinner s) {
        return ((Number) s.getValue()).intValue();
    }
}
