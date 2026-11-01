
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

    /** Called before the user leaves this page (other page, log out, close). Return false to stay. Override to warn about unsaved work. */
    public boolean confirmLeave() {
        return true;
    }
}
