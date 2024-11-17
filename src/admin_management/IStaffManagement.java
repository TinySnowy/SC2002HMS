package admin_management;

import user_management.User;
import java.util.List;

public interface IStaffManagement {
  void addStaffMember(User staff);

  void removeStaffMember(String staffId);

  void updateStaffMember(String staffId, User updatedStaff);

  List<User> getStaffByRole(String role);

  List<User> getAllStaff();

  void displayStaffList(List<User> staffList);
}