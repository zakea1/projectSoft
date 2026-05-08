package Service_Layer;

import Domain_Layer.Administrator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdministratorLoginTest {

    @Test
    public void testSingletonInstance() {
        AdministratorLogin instance1 = AdministratorLogin.getInstance();
        AdministratorLogin instance2 = AdministratorLogin.getInstance();
        assertSame(instance1, instance2, "Instances should be the same (singleton)");
    }

    @Test
    public void testSuccessfulLogin() {
        Administrator admin = new Administrator("admin@example.com", "12345");
        String result = AdministratorLogin.checkLogin("admin@example.com", "12345", admin);
        assertEquals("Login successful", result);
    }

    @Test
    public void testInvalidEmail() {
        Administrator admin = new Administrator("admin@example.com", "12345");
        String result = AdministratorLogin.checkLogin("wrong@example.com", "12345", admin);
        assertEquals("Invalid email or password", result);
    }

    @Test
    public void testInvalidPassword() {
        Administrator admin = new Administrator("admin@example.com", "12345");
        String result = AdministratorLogin.checkLogin("admin@example.com", "wrongpass", admin);
        assertEquals("Invalid email or password", result);
    }

    @Test
    public void testNullInputs() {
        Administrator admin = new Administrator("admin@example.com", "12345");
        assertEquals("Invalid email or password", AdministratorLogin.checkLogin(null, "12345", admin));
        assertEquals("Invalid email or password", AdministratorLogin.checkLogin("admin@example.com", null, admin));
        assertEquals("Invalid email or password", AdministratorLogin.checkLogin("admin@example.com", "12345", null));
    }
}
