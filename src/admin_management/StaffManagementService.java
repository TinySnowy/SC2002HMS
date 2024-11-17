package admin_management;

import user_management.*;
import java.util.*;

public class StaffManagementService implements IStaffManagement {
  private final UserController userController;
  private final StaffManager staffManager;

  public StaffManagementService(UserController userController, StaffManager staffManager) {
    this.userController = userController;
    this.staffManager = staffManager;
  }

  @Override
  public void addStaffMember(User staff) {
    validateStaffInput(staff);
    try {
      staffManager.addStaff(staff);
      System.out.println("Staff member added successfully: " + staff.getName());
    } catch (Exception e) {
      throw new IllegalStateException("Failed to add staff member: " + e.getMessage());
    }
  }

  @Override
  public void removeStaffMember(String staffId) {
    validateStaffId(staffId);
    try {
      staffManager.removeStaff(staffId);
      System.out.println("Staff member removed successfully");
    } catch (Exception e) {
      throw new IllegalStateException("Failed to remove staff member: " + e.getMessage());
    }
  }

  @Override
  public void updateStaffMember(String staffId, User updatedStaff) {
    validateStaffId(staffId);
    validateStaffInput(updatedStaff);
    if (!staffId.equals(updatedStaff.getId())) {
      throw new IllegalArgumentException("Staff ID cannot be changed");
    }
    try {
      staffManager.updateStaff(staffId, updatedStaff);
      System.out.println("Staff member updated successfully");
    } catch (Exception e) {
      throw new IllegalStateException("Failed to update staff member: " + e.getMessage());
    }
  }

  @Override
  public List<User> getStaffByRole(String role) {
    if (role == null || role.trim().isEmpty()) {
      throw new IllegalArgumentException("Role cannot be empty");
    }
    return staffManager.getStaffByRole(role);
  }

  @Override
  public List<User> getAllStaff() {
    return staffManager.getAllStaff();
  }

  public User handleStaffCreation(String name, String role, String password, String specialtyOrLicense, String gender,
      int age) {
    String prefix;
    switch (role.toLowerCase()) {
      case "doctor":
        prefix = "D";
        break;
      case "pharmacist":
        prefix = "P";
        break;
      case "administrator":
        prefix = "A";
        break;
      default:
        throw new IllegalArgumentException("Invalid role: " + role);
    }

    String newId = getNextAvailableId(prefix);
    return createStaffMember(newId, name, role, password, specialtyOrLicense, gender, age);
  }

  @Override
  public void displayStaffList(List<User> staffList) {
    if (staffList == null) {
      throw new IllegalArgumentException("Staff list cannot be null");
    }

    if (staffList.isEmpty()) {
      System.out.println("No staff members found.");
      return;
    }

    System.out.println("\nStaff List:");
    System.out.println("----------------------------------------");
    System.out.printf("%-10s %-20s %-15s %-20s%n", "ID", "Name", "Role", "Additional Info");
    System.out.println("----------------------------------------");

    for (User staff : staffList) {
      String additionalInfo = getAdditionalInfo(staff);
      System.out.printf("%-10s %-20s %-15s %-20s%n",
          staff.getId(),
          staff.getName(),
          staff.getRole(),
          additionalInfo);
    }
    System.out.println("----------------------------------------");
  }

  private User createStaffMember(String id, String name, String role, String password, String specialtyOrLicense,
      String gender, int age) {
    try {
      User newStaff = userController.createNewStaff(id, name, role, password, specialtyOrLicense, gender, age);
      if (newStaff != null) {
        addStaffMember(newStaff);
        return newStaff;
      }
      throw new IllegalArgumentException("Failed to create staff member");
    } catch (Exception e) {
      throw new IllegalStateException("Error creating staff member: " + e.getMessage());
    }
  }

  private String getNextAvailableId(String prefix) {
    List<User> existingStaff = getAllStaff();
    int maxId = 0;

    for (User staff : existingStaff) {
      String id = staff.getId();
      if (id.startsWith(prefix)) {
        try {
          int num = Integer.parseInt(id.substring(prefix.length()));
          maxId = Math.max(maxId, num);
        } catch (NumberFormatException ignored) {
        }
      }
    }

    return String.format("%s%03d", prefix, maxId + 1);
  }

  private void validateStaffInput(User staff) {
    if (staff == null) {
      throw new IllegalArgumentException("Staff cannot be null");
    }
    if (staff.getId() == null || staff.getId().trim().isEmpty()) {
      throw new IllegalArgumentException("Staff ID cannot be empty");
    }
    if (staff.getName() == null || staff.getName().trim().isEmpty()) {
      throw new IllegalArgumentException("Staff name cannot be empty");
    }
    if (staff instanceof Patient) {
      throw new IllegalArgumentException("Cannot manage patients through staff management");
    }
  }

  private void validateStaffId(String staffId) {
    if (staffId == null || staffId.trim().isEmpty()) {
      throw new IllegalArgumentException("Staff ID cannot be empty");
    }
  }

  private String getAdditionalInfo(User staff) {
    if (staff instanceof Doctor) {
      return "Specialty: " + ((Doctor) staff).getSpecialty();
    } else if (staff instanceof Pharmacist) {
      return "License: " + ((Pharmacist) staff).getLicenseNumber();
    }
    return "";
  }
}
