package Service_Layer;

import Persistence_Layer.UserAccountLoginChecker;

/**
 * UserLoginService - دالة واحدة فقط: فحص الدخول
 */
public class UserLoginService {
	  private static UserLoginService instance;

	    // كونستركتور خاص → يمنع إنشاء كائنات جديدة من الخارج
	    private UserLoginService() {
	        // أي تهيئة أولية
	    }

	    // دالة الوصول الوحيدة للنسخة
	    public static UserLoginService getInstance() {
	        if (instance == null) {
	            instance = new UserLoginService();
	        }
	        return instance;
	    }


    public String checkLogin(String email, String password) {
        UserAccountLoginChecker loginChecker =  UserAccountLoginChecker.getInstance();
        boolean isValid = loginChecker.checkCredentials(email, password);
        return isValid ? "Login successful" : "Invalid email or password";
    }
}