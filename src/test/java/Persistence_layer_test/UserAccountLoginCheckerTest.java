package Persistence_layer_test;

import org.junit.jupiter.api.*;

import Persistence_Layer.UserAccountLoginChecker;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountLoginCheckerTest {

    private static final String FILE_NAME = "users.txt";

    @BeforeEach
    void cleanUpBefore() throws Exception {
        Files.deleteIfExists(Paths.get(FILE_NAME));
    }

    @Test
    void testCheckCredentialsValid() throws Exception {
        Files.write(Paths.get(FILE_NAME), "test@example.com,password123".getBytes());
        UserAccountLoginChecker checker = UserAccountLoginChecker.getInstance();
        assertTrue(checker.checkCredentials("test@example.com", "password123"));
    }

    @Test
    void testCheckCredentialsWrongPassword() throws Exception {
        Files.write(Paths.get(FILE_NAME), "test@example.com,password123".getBytes());
        UserAccountLoginChecker checker = UserAccountLoginChecker.getInstance();
        assertFalse(checker.checkCredentials("test@example.com", "wrongpass"));
    }

    @Test
    void testCheckCredentialsWrongEmail() throws Exception {
        Files.write(Paths.get(FILE_NAME), "other@example.com,password456".getBytes());
        UserAccountLoginChecker checker = UserAccountLoginChecker.getInstance();
        assertFalse(checker.checkCredentials("test@example.com", "password456"));
    }

    @Test
    void testCheckCredentialsFileDoesNotExist() {
        UserAccountLoginChecker checker = UserAccountLoginChecker.getInstance();
        assertFalse(checker.checkCredentials("test@example.com", "password123"));
    }
}
