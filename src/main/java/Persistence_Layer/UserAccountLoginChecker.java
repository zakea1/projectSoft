package Persistence_Layer;

import java.io.*;

/**
 * UserAccountLoginChecker - دالة واحدة فقط: فحص بيانات الدخول
 */
public class UserAccountLoginChecker {

    private static final String FILE_NAME = "users.txt";
    private static UserAccountLoginChecker instance;

    // كونستركتور خاص → يمنع إنشاء كائنات جديدة من الخارج
    private UserAccountLoginChecker() {
        // أي تهيئة أولية
    }

    // دالة الوصول الوحيدة للنسخة
    public static UserAccountLoginChecker getInstance() {
        if (instance == null) {
            instance = new UserAccountLoginChecker();
        }
        return instance;
    }

    public boolean checkCredentials(String email, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    if (parts[0].equals(email) && parts[1].equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}