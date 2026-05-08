package Service_Layer;

import Domain_Layer.Administrator;

public class AdministratorLogin {

    private static AdministratorLogin instance;

    private AdministratorLogin() {}

    public static AdministratorLogin getInstance() {
        if (instance == null) {
            instance = new AdministratorLogin();
        }
        return instance;
    }

    public static String checkLogin(String email, String password, Administrator admin) {
        if (email == null || password == null || admin == null) {
            return "Invalid email or password";
        }
        if (admin.getEmail().equals(email) && admin.getPassword().equals(password)) {
            return "Login successful";
        }
        return "Invalid email or password";
    }
}
