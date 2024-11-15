package user_management;

public abstract class User {
    protected String id;
    protected String name;  // Add name field here
    protected String password;
    protected String role;
    protected boolean isFirstLogin;

    public User(String id, String password, String role, String name) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.name = name;
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

    public String getName() {
        return name;  // Getter for name
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    // New authenticate method
    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public abstract void displayMenu();
}
