
package common;

public class LoginContext {
    private static User currentUser = null;

    private LoginContext() {
    }

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
    public static void clear() { currentUser = null; }
}
