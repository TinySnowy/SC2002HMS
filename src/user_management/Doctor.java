package user_management;

/**
 * Model class representing a Doctor user in the HMS.
 * Extends User class with medical staff functionality.
 * Responsibilities:
 * - Patient care management
 * - Medical record access
 * - Appointment scheduling
 * - Prescription creation
 * - Treatment documentation
 * Provides medical staff-specific system access and capabilities.
 */
public class Doctor extends User {
    /** Medical specialty/department of the doctor */
    private String specialty;
    
    /** Schedule availability for appointments */
    private String availability;

    /**
     * Constructs a new Doctor user.
     * Initializes with medical staff privileges and department assignment.
     * 
     * @param id Unique doctor identifier
     * @param name Doctor's full name
     * @param rawPassword Initial password
     * @param specialty Medical specialty/department
     * @param gender Doctor's gender
     * @param age Doctor's age
     * @param isFirstLogin First login status flag
     */
    public Doctor(String id, String name, String rawPassword, String specialty,
            String gender, int age, boolean isFirstLogin) {
        super(id, name, "Doctor", rawPassword, gender, age, isFirstLogin);
        this.specialty = specialty;
    }

    /**
     * Retrieves doctor's medical specialty.
     * Used for department assignment and patient routing.
     * 
     * @return Medical specialty string
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Updates doctor's medical specialty.
     * Used for department reassignment.
     * 
     * @param specialty New medical specialty
     */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    /**
     * Retrieves doctor's schedule availability.
     * Used for appointment scheduling.
     * 
     * @return Availability string
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Updates doctor's schedule availability.
     * Used for managing appointment slots.
     * 
     * @param availability New availability schedule
     */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /**
     * Displays doctor-specific menu options.
     * Shows:
     * - Patient record access
     * - Appointment management
     * - Schedule control
     * - Treatment documentation
     * Provides access to medical staff functions.
     */
    public void displayMenu() {
        System.out.println("Doctor Menu:");
        System.out.println("1. View Patient Medical Records");
        System.out.println("2. Update Patient Medical Records");
        System.out.println("3. View Personal Schedule");
        System.out.println("4. Set Availability for Appointments");
        System.out.println("5. Accept or Decline Appointment Requests");
        System.out.println("6. View Upcoming Appointments");
        System.out.println("7. Record Appointment Outcome");
        System.out.println("8. Logout");
    }

    /**
     * Returns user type identifier.
     * Used for access control and permission management.
     * 
     * @return "Doctor" as user type
     */
    @Override
    public String getUserType() {
        return "Doctor";
    }
}
