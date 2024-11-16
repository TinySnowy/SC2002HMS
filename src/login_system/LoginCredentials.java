package login_system;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import utils.PasswordUtil;

import java.util.*;

public class LoginCredentials {
  private Map<String, LoginInfo> loginDatabase;
  private static final String LOGIN_FILE = "SC2002HMS/data/Login.csv";

  private static class LoginInfo {
    String id;
    String role;
    String passwordHash;
    boolean isFirstLogin;

    LoginInfo(String id, String role, String passwordHash, boolean isFirstLogin) {
      this.id = id;
      this.role = role;
      this.passwordHash = passwordHash;
      this.isFirstLogin = isFirstLogin;
    }
  }

  public LoginCredentials() {
    this.loginDatabase = new HashMap<>();
    loadLoginData();
  }

  private void loadLoginData() {
    List<String[]> records = CSVReaderUtil.readCSV(LOGIN_FILE);
    boolean isFirstRow = true;

    // If file doesn't exist or is empty, create initial login data
    if (records.isEmpty()) {
      initializeLoginData();
      return;
    }

    for (String[] record : records) {
      if (isFirstRow) {
        isFirstRow = false;
        continue;
      }

      if (record.length >= 4) {
        String id = record[0];
        String role = record[1];
        String passwordHash = record[2];
        boolean isFirstLogin = Boolean.parseBoolean(record[3]);
        loginDatabase.put(id, new LoginInfo(id, role, passwordHash, isFirstLogin));
      }
    }
  }

  private void initializeLoginData() {
    // Initialize with default password "password" for all users
    String defaultPasswordHash = PasswordUtil.hashPassword("password");

    CSVWriterUtil.writeCSV(LOGIN_FILE, writer -> {
      writer.write("ID,Role,PasswordHash,FirstLogin\n");
      // Add doctors
      writer.write(String.format("D001,Doctor,%s,true\n", defaultPasswordHash));
      writer.write(String.format("D002,Doctor,%s,true\n", defaultPasswordHash));
      // Add pharmacists
      writer.write(String.format("P001,Pharmacist,%s,true\n", defaultPasswordHash));
      writer.write(String.format("P002,Pharmacist,%s,true\n", defaultPasswordHash));
      // Add patients
      writer.write(String.format("P1001,Patient,%s,true\n", defaultPasswordHash));
      writer.write(String.format("P1002,Patient,%s,true\n", defaultPasswordHash));
      writer.write(String.format("P1003,Patient,%s,true\n", defaultPasswordHash));
      writer.write(String.format("P003,Patient,%s,true\n", defaultPasswordHash));
      writer.write(String.format("P004,Patient,%s,true\n", defaultPasswordHash));
      writer.write(String.format("P005,Patient,%s,true\n", defaultPasswordHash));
    });
    loadLoginData(); // Reload the data
  }

  public boolean authenticateUser(String id, String password) {
    LoginInfo info = loginDatabase.get(id);
    if (info == null)
      return false;
    return PasswordUtil.verifyPassword(password, info.passwordHash);
  }

  public String getUserRole(String id) {
    LoginInfo info = loginDatabase.get(id);
    return info != null ? info.role : null;
  }

  public boolean isFirstLogin(String id) {
    LoginInfo info = loginDatabase.get(id);
    return info != null && info.isFirstLogin;
  }

  public void updatePassword(String id, String newPassword) {
    LoginInfo info = loginDatabase.get(id);
    if (info != null) {
      info.passwordHash = PasswordUtil.hashPassword(newPassword);
      info.isFirstLogin = false;
      saveLoginData();
    }
  }

  private void saveLoginData() {
    CSVWriterUtil.writeCSV(LOGIN_FILE, writer -> {
      writer.write("ID,Role,PasswordHash,FirstLogin\n");
      for (LoginInfo info : loginDatabase.values()) {
        writer.write(String.format("%s,%s,%s,%b\n",
            info.id, info.role, info.passwordHash, info.isFirstLogin));
      }
    });
  }
}