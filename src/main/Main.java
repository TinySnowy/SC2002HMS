package main;

import doctor_management.DoctorDashboard;
import appointment_management.Appointment;
import appointment_management.AppointmentList;
import user_management.Doctor;
import user_management.Patient;
import pharmacy_management.Prescription;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Initialize a sample doctor
        Doctor doctor = new Doctor("D1001", "password", "Dr. John Doe", "Cardiology");
        doctor.setAvailability("Mon-Fri: 9 AM - 5 PM");

        // Initialize a sample patient
        Patient patient = new Patient("P1001", "password", "Alice Smith", "123456789", "alice@example.com");

        // Initialize AppointmentList and add sample appointments
        AppointmentList appointmentList = new AppointmentList();
        Appointment appointment1 = new Appointment("A1001", patient, doctor, LocalDateTime.now().plusDays(1));
        Appointment appointment2 = new Appointment("A1002", patient, doctor, LocalDateTime.now().plusDays(2));

        appointmentList.addAppointment(appointment1);
        appointmentList.addAppointment(appointment2);

        // Create the DoctorDashboard
        DoctorDashboard doctorDashboard = new DoctorDashboard(doctor, appointmentList);

        // Simulate the doctor's interaction with the dashboard
        doctorDashboard.showDashboard();
    }
}
