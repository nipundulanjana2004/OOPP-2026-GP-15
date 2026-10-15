
package common;

import common.ui.Dialogs;
import common.ui.LoginFrame;
import common.ui.Theme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Theme.install();
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseInitializer.ensureReady();
                new LoginFrame().setVisible(true);
            } catch (DatabaseException e) {
                e.printStackTrace();
                Dialogs.error(null, e.getMessage() + "\n\nOpen db.properties, set your MySQL user/password, start MySQL and run again.");
                System.exit(1);
            }
        });
    }
}
