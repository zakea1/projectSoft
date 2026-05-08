package Persistence_layer_test;


import org.junit.jupiter.api.*;

import Persistence_Layer.UserAccountExistsChecker;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountExistsCheckerTest {

    private static final String FILE_NAME = "users.txt";

    @BeforeEach
    void cleanUpBefore() throws Exception {
        Files.deleteIfExists(Paths.get(FILE_NAME));
    }

    @Test
    void testUserExistsTrue() throws Exception {
        Files.write(Paths.get(FILE_NAME), "test@example.com,password123".getBytes());
        UserAccountExistsChecker checker = UserAccountExistsChecker.getInstance();
        assertTrue(checker.userExists("test@example.com"));
    }

    @Test
    void testUserExistsFalse() throws Exception {
        Files.write(Paths.get(FILE_NAME), "other@example.com,password456".getBytes());
        UserAccountExistsChecker checker = UserAccountExistsChecker.getInstance();
        assertFalse(checker.userExists("test@example.com"));
    }

    @Test
    void testUserExistsWhenFileDoesNotExist() {
        UserAccountExistsChecker checker = UserAccountExistsChecker.getInstance();
        assertFalse(checker.userExists("test@example.com"));
    }
}
