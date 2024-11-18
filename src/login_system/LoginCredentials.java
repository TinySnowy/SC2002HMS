package login_system;

import utils.CSVReaderUtil;
import utils.CSVWriterUtil;
import utils.PasswordUtil;

import java.util.*;

public class LoginCredentials {
   private Map<String, LoginInfo> loginDatabase;
   private static final String LOGIN_FILE = "SC2002HMS/data/Login.csv";
   private static boolean isInitializing = false;

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
       if (isInitializing) return;
       
       List<String[]> records = CSVReaderUtil.readCSV(LOGIN_FILE);
       boolean isFirstRow = true;

       if (records.isEmpty()) {
           isInitializing = true;
           initializeLoginData();
           isInitializing = false;
           return;
       }

       loginDatabase.clear();
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
       String defaultPasswordHash = PasswordUtil.hashPassword("password");
       loginDatabase.clear();

       // Initialize default users in memory first
       String[][] defaultUsers = {
           {"D001", "Doctor"}, {"D002", "Doctor"},
           {"P001", "Pharmacist"}, {"P002", "Pharmacist"},
           {"P1001", "Patient"}, {"P1002", "Patient"}, {"P1003", "Patient"},
           {"P003", "Patient"}, {"P004", "Patient"}, {"P005", "Patient"}
       };

       // Add users to database
       for (String[] user : defaultUsers) {
           loginDatabase.put(user[0], new LoginInfo(user[0], user[1], defaultPasswordHash, true));
       }

       // Save to file
       saveLoginData();
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