/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : A styled data table with a live search box (zebra rows, coloured status words, sortable).
 *  PLUGS IN: Every list/report screen of M1, M2, M3, M4 uses it - nobody re-writes JTable code.
 *  OOP     : Composition + inheritance (extends Card), overriding the cell renderer.
 * ====================================================================
 */
package common.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TablePanel extends Card {
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final Set<Integer> editable = new HashSet<>();
    private final JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    private final JLabel countLabel = Theme.muted("");
    private final Set<Integer> fixedWidth = new HashSet<>();
    private JScrollPane scroll;

    public TablePanel(String title, String... columns) {
        super(new BorderLayout(0, 10));
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return editable.contains(c);
            }

            @Override
            public Class<?> getColumnClass(int c) {
                return getRowCount() > 0 && getValueAt(0, c) instanceof Boolean ? Boolean.class : Object.class;
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        styleTable();

        JPanel top = Theme.transparent(new BorderLayout(10, 0));
        JPanel left = Theme.transparent(new BorderLayout());
        if (title != null && !title.isEmpty()) left.add(Theme.heading(title), BorderLayout.WEST);
        left.add(countLabel, BorderLayout.CENTER);
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        top.add(left, BorderLayout.WEST);

        JTextField search = Theme.text(14);
        search.putClientProperty("JTextField.placeholderText", "Search...");
        search.setToolTipText("Type to filter the rows");
        search.getDocument().addDocumentListener(new DocumentListener() {
            private void apply() {
                String t = search.getText().trim();
                sorter.setRowFilter(t.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(t)));
                updateCount();
            }

            @Override public void insertUpdate(DocumentEvent e) { apply(); }
            @Override public void removeUpdate(DocumentEvent e) { apply(); }
            @Override public void changedUpdate(DocumentEvent e) { apply(); }
        });
        toolbar.setOpaque(false);
        toolbar.add(search);
        top.add(toolbar, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(table);
        scroll = sp;
        sp.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustResize();
            }
        });
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);
    }

    private void styleTable() {
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(Theme.BASE);
        table.setSelectionBackground(Theme.SELECT);
        table.setSelectionForeground(Theme.TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JTableHeader h = table.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(100, 36));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, r, c);
                l.setBackground(new Color(0xEAF0FC));
                l.setForeground(Theme.TEXT);
                l.setFont(Theme.BOLD);
                l.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER), BorderFactory.createEmptyBorder(0, 10, 0, 10)));
                return l;
            }
        });
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, false, r, c);
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                l.setBackground(sel ? Theme.SELECT : (r % 2 == 0 ? Color.WHITE : Theme.ZEBRA));
                Color sc = Theme.statusColor(v == null ? null : v.toString());
                l.setForeground(sc != null ? sc : Theme.TEXT);
                l.setFont(sc != null ? Theme.BOLD : Theme.BASE);
                return l;
            }
        });
    }

    // ------------------------------ API ------------------------------
    public void setRows(List<Object[]> rows) {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        model.setRowCount(0);
        for (Object[] r : rows) model.addRow(r);
        fitColumns();
        updateCount();
    }

    /** Sizes each column to its header / content (max 320 px) so names are not cut off. */
    private void fitColumns() {
        FontMetrics fm = table.getFontMetrics(Theme.BOLD);
        for (int c = 0; c < table.getColumnCount(); c++) {
            if (fixedWidth.contains(c)) continue;
            int w = fm.stringWidth(String.valueOf(model.getColumnName(c))) + 30;
            for (int r = 0; r < Math.min(model.getRowCount(), 60); r++) {
                Object v = model.getValueAt(r, c);
                w = Math.max(w, fm.stringWidth(v == null ? "" : v.toString()) + 30);
            }
            table.getColumnModel().getColumn(c).setPreferredWidth(Math.max(60, Math.min(w, 320)));
        }
        adjustResize();
    }

    /** Wide tables scroll sideways instead of squashing every column. */
    private void adjustResize() {
        if (scroll == null) return;
        int total = 0;
        for (int c = 0; c < table.getColumnCount(); c++) total += table.getColumnModel().getColumn(c).getPreferredWidth();
        table.setAutoResizeMode(total > scroll.getViewport().getWidth() ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void updateCount() {
        countLabel.setText(table.getRowCount() + " rows");
    }

    public void setEditableColumns(int... cols) {
        editable.clear();
        for (int c : cols) editable.add(c);
    }

    public void setColumnWidth(int col, int width) {
        if (col >= 0 && col < table.getColumnModel().getColumnCount()) {
            fixedWidth.add(col);
            table.getColumnModel().getColumn(col).setPreferredWidth(width);
        }
    }

    public void addToolbarComponent(JComponent c) {
        toolbar.add(c, 0);
    }

    public JTable getTable() { return table; }
    public DefaultTableModel getModel() { return model; }

    /** Selected row as an index into the DATA (-1 if none), independent of sorting/filtering. */
    public int getSelectedModelRow() {
        int v = table.getSelectedRow();
        return v < 0 ? -1 : table.convertRowIndexToModel(v);
    }

    public void onSelect(Runnable r) {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) r.run();
        });
    }

    public void stopEditing() {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
    }
}
