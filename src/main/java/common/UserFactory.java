/*
 * ====================================================================
 *  PART    : COMMON (shared by M1-M4)
 *  PURPOSE : Builds the correct User subclass for a role. Looks the class up by name so the project still compiles when a member's zip is not installed yet.
 *  PLUGS IN: Role -> admin.Admin (M1) / lecturer.Lecturer (M2) / officer.TechnicalOfficer (M3) / undergraduate.Undergraduate (M4).
 *  OOP     : POLYMORPHISM (callers only see 'User'), Factory pattern, reflection.
 * ====================================================================
 */
package common;

public class UserFactory {

    private UserFactory() {
    }

    public static User create(Role role) throws AppException {
        try {
            Class<?> c = Class.forName(role.getModelClass());
            return (User) c.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new ModuleMissingException("The " + role.getLabel() + " module is not installed in this build ("
                    + role.getModelClass() + " is missing). Extract that member's zip into the project.");
        } catch (ReflectiveOperationException e) {
            throw new AppException("Could not create a " + role.getLabel() + " object.", e);
        }
    }
}
