package Persistence_Layer;

import java.io.*;

public class UserAccountSaver {

    private static final String FILE_NAME = "users.txt";
    private static UserAccountSaver instance;

   
    private UserAccountSaver() {
   
    }

    public static UserAccountSaver getInstance() {
        if (instance == null) {
            instance = new UserAccountSaver();
        }
        return instance;
    }


    public void saveUser(String email, String password) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(email + "," + password);
            writer.newLine();
        }
    }
}