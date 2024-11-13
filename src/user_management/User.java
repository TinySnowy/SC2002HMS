package user_management;

public abstract class User {
    protected String id;
    protected String password;
    protected String role;
    protected boolean isFirstLogin;

    public User(String id, String password, String role) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.isFirstLogin = true;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    // Abstract method for each role's specific menu
    public abstract void displayMenu();
}
