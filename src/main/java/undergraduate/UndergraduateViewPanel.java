
package undergraduate;

import common.AppException;
import common.ui.BasePanel;
import common.ui.Dialogs;

public abstract class UndergraduateViewPanel extends BasePanel {
    protected final Undergraduate student;

    protected UndergraduateViewPanel(Undergraduate student, String title, String subtitle) {
        super(title, subtitle);
        this.student = student;
    }

    /** Fill the page with THIS student's data. */
    protected abstract void loadData() throws AppException;

    protected static String pct(double v) {
        return String.format("%.1f%%", v);
    }

    @Override
    public final void onShow() {
        try {
            loadData();
        } catch (AppException e) {
            Dialogs.handle(this, e);
        }
    }
}
