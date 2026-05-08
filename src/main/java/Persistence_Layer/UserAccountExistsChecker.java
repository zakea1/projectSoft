package Persistence_Layer;

import java.io.*;

/**
 * UserAccountExistsChecker - دالة واحدة فقط: التحقق من وجود المستخدم
 */
public class UserAccountExistsChecker {

    private static final String FILE_NAME = "users.txt";

    private static UserAccountExistsChecker instance;

    // كونستركتور خاص → يمنع إنشاء كائنات جديدة من الخارج
    private UserAccountExistsChecker() {
        // أي تهيئة أولية
    }

    // دالة الوصول الوحيدة للنسخة
    public static UserAccountExistsChecker getInstance() {
        if (instance == null) {
            instance = new UserAccountExistsChecker();
        }
        return instance;
    }
    public boolean userExists(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}