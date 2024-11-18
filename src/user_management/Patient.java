package user_management;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Patient extends User {
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String contactInfo;
    private String email;

    public Patient(String id, String name, String password, LocalDate dateOfBirth, 
                  String gender, String bloodGroup, String contactInfo, String email) {
        super(id, name, password, "Patient");
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGroup = bloodGroup;
        this.contactInfo = contactInfo;
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("Patient ID: %s\nName: %s\nDOB: %s\nGender: %s\nBlood Group: %s\n" +
                           "Contact: %s\nEmail: %s",
                           getId(), getName(), 
                           dateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                           gender, bloodGroup, contactInfo, email);
    }

    @Override
    public String getUserType() {
        return "Patient";
    }
}