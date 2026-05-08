
package Service_Layer;

import java.io.IOException;
import java.util.ArrayList;

import Domain_Layer.User;
import Persistence_Layer.UserAccountExistsChecker;
import Persistence_Layer.UserAccountSaver;
public class UserRegistrationService {
	  private static UserRegistrationService instance;

	    // كونستركتور خاص → يمنع إنشاء كائنات جديدة من الخارج
	    private UserRegistrationService() {
	        // أي تهيئة أولية
	    }

	    // دالة الوصول الوحيدة للنسخة
	    public static UserRegistrationService getInstance() {
	        if (instance == null) {
	            instance = new UserRegistrationService();
	        }
	        return instance;
	    }

public String registerUser(String email, String password) {
	
    User newUser = new User(email, password); // كيان من الدومين

    UserAccountExistsChecker existsChecker =  UserAccountExistsChecker.getInstance();

    if (existsChecker.userExists(newUser.getEmail())) {
        return "Account already exists!";
    }

    UserAccountSaver saver =  UserAccountSaver.getInstance();
    try {
        saver.saveUser(newUser.getEmail(), newUser.getPassword());
        return "Account created successfully!";
    } catch (IOException e) {
        return "Error saving account: " + e.getMessage();
    }
}
}
