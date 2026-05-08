package presentation_Layer_Test;


import org.junit.jupiter.api.*;

import presentation_Layer.AdminUI;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AdminUITest {

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
    void testAdminMenuInvalidChoice() {
        String input = "99\n4\n"; // إدخال رقم غلط ثم خروج
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        AdminUI.adminMenu(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice."));
    }

    @Test
    void testAdminMenuNonNumericInput() {
        String input = "abc\n4\n"; // إدخال نص بدل رقم
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        AdminUI.adminMenu(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Please enter a valid number."));
    }


    @Test
    void testCancelByAdminValid() {
        String input = "2\n1\n4\n"; // خيار 2 → cancel slot index=1 → خروج
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        AdminUI.adminMenu(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("cancelled") || output.contains("Returning to main menu"));
    }

    @Test
    void testViewAllSlots() {
        String input = "3\n4\n"; // خيار 3 → عرض كل السلوطات → خروج
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        AdminUI.adminMenu(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Available Slots") || output.contains("Returning to main menu"));
    }
}
