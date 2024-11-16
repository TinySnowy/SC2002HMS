package user_management;

import utils.PasswordUtil;

public abstract class User {
    protected String id;
    protected String name;
    protected String role;
    private String passwordHash;
    private boolean isFirstLogin;

    public User(String id, String name, String role, String rawPassword, boolean isFirstLogin) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.passwordHash = PasswordUtil.hashPassword(rawPassword);
        this.isFirstLogin = isFirstLogin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public boolean authenticate(String inputPassword) {
        return PasswordUtil.verifyPassword(inputPassword, this.passwordHash);
    }

    public void setPassword(String newPassword) {
        this.passwordHash = PasswordUtil.hashPassword(newPassword);
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.isFirstLogin = firstLogin;
    }
}
