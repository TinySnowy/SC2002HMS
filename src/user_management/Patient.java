package user_management;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Model class representing a Patient user in the HMS.
 * Extends User class with patient-specific functionality.
 * Contains:
 * - Personal medical information
 * - Contact details
 * - Doctor assignments
 * - Medical history access
 * Provides patient-level system access and information management.
 */
public class Patient extends User {
    /** Patient's recorded date of birth */
    private LocalDate dateOfBirth;
    
    /** Patient's gender identification */
    private String gender;
    
    /** Patient's blood type classification */
    private String bloodGroup;
    
    /** Patient's contact information (phone/address) */
    private String contactInfo;
    
    /** Patient's email address for notifications */
    private String email;
    
    /** Assigned primary care physician */
    private Doctor doctor;

    /**
     * Constructs a new Patient user with medical information.
     * Initializes patient record with essential health data.
     * 
     * @param id Unique patient identifier
     * @param name Patient's full name
     * @param password Account password
     * @param dateOfBirth Patient's birth date
     * @param gender Patient's gender
     * @param bloodGroup Patient's blood type
     * @param contactInfo Contact details
     * @param email Email address
     */
    public Patient(String id, String name, String password, LocalDate dateOfBirth, 
                  String gender, String bloodGroup, String contactInfo, String email) {
        super(id, name, password, "Patient");
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.contactInfo = contactInfo;
        this.email = email;
    }

    /**
     * Retrieves patient's date of birth.
     * Used for age calculation and medical records.
     * 
     * @return Birth date
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Updates patient's date of birth.
     * Used for record correction.
     * 
     * @param dateOfBirth New birth date
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Retrieves patient's gender.
     * Used for medical context and records.
     * 
     * @return Gender identification
     */
    public String getGender() {
        return gender;
    }

    /**
     * Updates patient's gender.
     * Used for record maintenance.
     * 
     * @param gender New gender value
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Retrieves patient's blood type.
     * Critical for medical procedures.
     * 
     * @return Blood group classification
     */
    public String getBloodGroup() {
        return bloodGroup;
    }

    /**
     * Updates patient's blood group.
     * Used for medical record accuracy.
     * 
     * @param bloodGroup New blood type
     */
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    /**
     * Retrieves patient's contact information.
     * Used for communication and emergencies.
     * 
     * @return Contact details
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * Updates patient's contact information.
     * Maintains current contact details.
     * 
     * @param contactInfo New contact information
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * Retrieves patient's email address.
     * Used for notifications and communication.
     * 
     * @return Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Updates patient's email address.
     * Maintains communication channel.
     * 
     * @param email New email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves assigned primary doctor.
     * Used for medical care coordination.
     * 
     * @return Assigned physician
     */
    public Doctor getDoctor() {
        return doctor;
    }

    /**
     * Updates patient's assigned doctor.
     * Manages primary care relationship.
     * 
     * @param doctor New primary physician
     */
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /**
     * Formats patient information for display.
     * Includes all relevant medical and contact details.
     * 
     * @return Formatted patient information string
     */
    @Override
    public String toString() {
        return String.format("Patient ID: %s\nName: %s\nDOB: %s\nGender: %s\nBlood Group: %s\n" +
                           "Contact: %s\nEmail: %s",
                           getId(), getName(), 
                           dateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                           gender, bloodGroup, contactInfo, email);
    }

    /**
     * Returns user type identifier.
     * Used for access control and permissions.
     * 
     * @return "Patient" as user type
     */
    @Override
    public String getUserType() {
        return "Patient";
    }
}