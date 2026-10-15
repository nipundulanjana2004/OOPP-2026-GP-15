/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : ABSTRACT parent of every page (title + subtitle + body). onShow() is called each time the page is opened so data is always fresh.
 *  PLUGS IN: ALL pages of M1, M2, M3, M4 extend it (e.g. M1 ManagementPanel, M2 MarkEntryPanel, M3 AttendanceEntryPanel, M4 MyGradesPanel).
 *  OOP     : ABSTRACTION + INHERITANCE + POLYMORPHISM (each page overrides onShow()).
 * ====================================================================
 */
package common.ui;

import javax.swing.*;
import java.awt.*;

public abstract class BasePanel extends JPanel {
    protected final JPanel body = Theme.transparent(new BorderLayout(0, 14));

    public BasePanel(String title, String subtitle) {
        super(new BorderLayout(0, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JPanel head = Theme.transparent(new GridLayout(0, 1, 0, 2));
        head.add(Theme.title(title));
        if (subtitle != null && !subtitle.isEmpty()) head.add(Theme.muted(subtitle));
        add(head, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    /** Called every time the page becomes visible. Override to (re)load data. */
    public void onShow() {
    }
}
