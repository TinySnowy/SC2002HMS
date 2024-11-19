package user_management;

import utils.PasswordUtil;

/**
 * Abstract base class for all user types in the HMS.
 * Provides:
 * - Core user attributes
 * - Authentication handling
 * - Access control
 * - Profile management
 * Serves as foundation for specific user roles (Doctor, Patient, etc.).
 */
public abstract class User {
    /** Unique identifier for the user */
    protected String id;
    
    /** User's full name */
    protected String name;
    
    /** User's system role (Doctor, Patient, etc.) */
    protected String role;
    
    /** User's gender identification */
    protected String gender;
    
    /** User's age in years */
    protected int age;
    
    /** Hashed password for authentication */
    private String passwordHash;
    
    /** Flag indicating first-time login status */
    private boolean isFirstLogin;

    /**
     * Constructs a new user with complete profile.
     * Initializes all user attributes and security settings.
     * 
     * @param id Unique user identifier
     * @param name User's full name
     * @param role System role assignment
     * @param rawPassword Initial unhashed password
     * @param gender User's gender
     * @param age User's age
     * @param isFirstLogin First login status
     * @throws IllegalArgumentException if required parameters invalid
     */
    public User(String id, String name, String role, String rawPassword, String gender, int age, boolean isFirstLogin) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.gender = gender;
        this.age = age;
        this.passwordHash = PasswordUtil.hashPassword(rawPassword);
        this.isFirstLogin = isFirstLogin;
    }

    /**
     * Constructs a new user with minimal profile.
     * Used for basic user creation with essential details.
     * 
     * @param id Unique user identifier
     * @param name User's full name
     * @param password Initial password
     * @param role System role assignment
     */
    public User(String id, String name, String password, String role) {
        this.id = id;
        this.name = name;
        this.passwordHash = PasswordUtil.hashPassword(password);
        this.role = role;
    }

    /**
     * Retrieves user's unique identifier.
     * Used for system identification and tracking.
     * 
     * @return User ID string
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves user's full name.
     * Used for display and identification.
     * 
     * @return User's name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves user's system role.
     * Used for access control and permissions.
     * 
     * @return Role designation
     */
    public String getRole() {
        return role;
    }

    /**
     * Retrieves user's gender.
     * Used for profile information.
     * 
     * @return Gender identification
     */
    public String getGender() {
        return gender;
    }

    /**
     * Retrieves user's age.
     * Used for profile information.
     * 
     * @return Age in years
     */
    public int getAge() {
        return age;
    }

    /**
     * Authenticates user login attempt.
     * Verifies password against stored hash.
     * 
     * @param inputPassword Password to verify
     * @return true if authentication successful
     */
    public boolean authenticate(String inputPassword) {
        return PasswordUtil.verifyPassword(inputPassword, this.passwordHash);
    }

    /**
     * Updates user's password.
     * Hashes and stores new password.
     * 
     * @param newPassword New password to set
     */
    public void setPassword(String newPassword) {
        this.passwordHash = PasswordUtil.hashPassword(newPassword);
    }

    /**
     * Retrieves hashed password.
     * Used for authentication processes.
     * 
     * @return Hashed password string
     */
    public String getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Checks first login status.
     * Used for password change requirements.
     * 
     * @return true if first login pending
     */
    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    /**
     * Updates first login status.
     * Used after initial password change.
     * 
     * @param firstLogin New status value
     */
    public void setFirstLogin(boolean firstLogin) {
        this.isFirstLogin = firstLogin;
    }

    /**
     * Abstract method for user type identification.
     * Must be implemented by specific user classes.
     * 
     * @return User type string
     */
    public abstract String getUserType();
}