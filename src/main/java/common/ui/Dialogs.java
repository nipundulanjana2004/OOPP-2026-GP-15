/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : Friendly message boxes. handle() shows AppException text only - never a stack trace (NFR-10).
 *  PLUGS IN: Called by every panel of M1-M4 in their catch blocks.
 *  OOP     : Exception handling in one reusable place.
 * ====================================================================
 */
package common.ui;

import common.AppException;

import javax.swing.*;
import java.awt.Component;

public class Dialogs {
    private Dialogs() {
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Faculty of Technology MIS", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Please confirm", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** One catch-all for the GUI: our own exceptions show their message, anything else is logged. */
    public static void handle(Component parent, Exception e) {
        if (e instanceof AppException) {
            error(parent, ((AppException) e).handle());
        } else {
            e.printStackTrace();
            error(parent, "Something unexpected happened. Please try again.");
        }
    }
}
