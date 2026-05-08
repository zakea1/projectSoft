package presentation_Layer_Test;

import org.junit.jupiter.api.*;
import presentation_Layer.UserUI;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UserUITest {

    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }

    @Test
    void testIsValidEmailValid() {
        assertEquals("valid email format", UserUI.isValidEmail("test@example.com"));
    }

    @Test
    void testIsValidEmailInvalid() {
        assertEquals("Invalid email format", UserUI.isValidEmail("wrong-email"));
    }

    @Test
    void testUserAccountMenuInvalidChoice() {
        String input = "abc\n"; 
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserUI.userAccountMenu(scanner);
        assertTrue(outContent.toString().contains("Invalid choice."));
    }

    @Test
    void testUserAccountMenuOutOfRangeChoice() {
        String input = "9\n"; 
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserUI.userAccountMenu(scanner);
        assertTrue(outContent.toString().contains("Invalid choice."));
    }



    @Test
    void testUserModeViewSlots() {
        String input = "1\n5\n"; 
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserUI.userMode(scanner, "user@example.com");
        assertTrue(outContent.toString().contains("Returning to main menu"));
    }

 

    @Test
    void testUserModeCancelAppointment() {
        String input = "4\n5\n"; 
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserUI.userMode(scanner, "user@example.com");
        assertTrue(outContent.toString().contains("Returning to main menu"));
    }

    @Test
    void testUserModeInvalidChoice() {
        String input = "99\n5\n"; 
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserUI.userMode(scanner, "user@example.com");
        assertTrue(outContent.toString().contains("Invalid choice."));
    }

    @Test
    void testUserModeNonNumericInput() {
        String input = "abc\n5\n"; 
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        UserUI.userMode(scanner, "user@example.com");
        assertTrue(outContent.toString().contains("Please enter a valid number."));
    }
}
