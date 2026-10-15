/*
 * ====================================================================
 *  PART    : COMMON UI (shared by M1-M4)
 *  PURPOSE : After login, opens the dashboard that belongs to the user's role. Loads the M-part class by name, so a missing member zip gives a clear message instead of a compile error.
 *  PLUGS IN: LoginFrame -> RoleRouter -> AdminDashboard (M1) / LecturerDashboard (M2) / OfficerDashboard (M3) / UndergraduateDashboard (M4).
 *  OOP     : POLYMORPHISM (every dashboard is just a JFrame/DashboardFrame), reflection.
 * ====================================================================
 */
package common.ui;

import common.AppException;
import common.User;

import javax.swing.*;

public class RoleRouter {

    private RoleRouter() {
    }

    public static JFrame openDashboard(User user) throws AppException {
        String className = user.getRole().getDashboardClass();
        try {
            Class<?> c = Class.forName(className);
            return (JFrame) c.getConstructor(User.class).newInstance(user);
        } catch (ClassNotFoundException e) {
            throw new AppException("The " + user.getRole().getLabel() + " screens are not installed yet (" + className
                    + " is missing). Extract that member's zip into the project and rebuild.");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new AppException("Could not open the " + user.getRole().getLabel() + " dashboard.", e);
        }
    }
}
