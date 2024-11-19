package patient_management.interfaces;

/**
 * Interface defining the contract for personal information management operations.
 * Specifies required functionality for:
 * - Personal information updates
 * - Information display
 * - Data validation
 * - Information security
 * Provides standardized methods for managing personal information.
 */
public interface IPersonalInfoManager {
    /**
     * Updates personal information for the current user.
     * Implementation should:
     * - Allow contact info updates
     * - Allow email updates
     * - Validate input data
     * - Persist changes
     * - Handle validation errors
     * - Ensure data security
     * - Maintain update history
     * - Notify relevant systems
     */
    void updatePersonalInfo();

    /**
     * Displays current personal information.
     * Implementation should:
     * - Show user details
     * - Display contact info
     * - Show email address
     * - Format output clearly
     * - Handle missing data
     * - Ensure privacy
     * - Mask sensitive data
     * - Present organized view
     */
    void displayCurrentInfo();
}
