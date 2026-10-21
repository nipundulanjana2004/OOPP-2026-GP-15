
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
