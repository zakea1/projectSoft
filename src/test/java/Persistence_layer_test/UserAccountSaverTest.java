package Persistence_layer_test;

import org.junit.jupiter.api.*;

import Persistence_Layer.UserAccountSaver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountSaverTest {

    private static final String FILE_NAME = "users.txt";

    @BeforeEach
    void cleanUpBefore() throws Exception {
        Files.deleteIfExists(Paths.get(FILE_NAME));
    }

    @Test
    void testSaveSingleUser() throws Exception {
        UserAccountSaver saver = UserAccountSaver.getInstance();
        saver.saveUser("test@example.com", "password123");

        List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
        assertEquals(1, lines.size());
        assertEquals("test@example.com,password123", lines.get(0));
    }

    @Test
    void testSaveMultipleUsers() throws Exception {
        UserAccountSaver saver = UserAccountSaver.getInstance();
        saver.saveUser("user1@example.com", "pass1");
        saver.saveUser("user2@example.com", "pass2");

        List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
        assertEquals(2, lines.size());
        assertEquals("user1@example.com,pass1", lines.get(0));
        assertEquals("user2@example.com,pass2", lines.get(1));
    }
}
