package presentation_Layer;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Persistence_Layer.AppointmentSlotsManager;
import Service_Layer.UserLoginService;
import Service_Layer.UserRegistrationService;
import Service_Layer.AppointmentBookingService;
import Service_Layer.AppointmentCancellationService;
import Service_Layer.AppointmentModificationService;

/**
 * UserUI - Presentation Layer
 */
public class UserUI {

    private static final AppointmentBookingService bookingService = AppointmentBookingService.getInstance();
    private static final AppointmentCancellationService cancelService = AppointmentCancellationService.getInstance();
    private static final AppointmentModificationService modifyService = AppointmentModificationService.getInstance();
    private static final AppointmentSlotsManager slots = AppointmentSlotsManager.getInstance();

    // =========================
    // User Account Menu
    // =========================
    public static void userAccountMenu(Scanner input) {
        System.out.println("\n=== User Account ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Enter choice: ");
        int choice;
        try {
            choice = Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }

        if (choice == 1) {
            userLogin(input);
        } else if (choice == 2) {
            userRegister(input);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // =========================
    // Email Validation
    // =========================
    public static String isValidEmail(String email) {
        email = email.trim();
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Matcher matcher = Pattern.compile(regex).matcher(email);
        return matcher.matches() ? "valid email format" : "Invalid email format";
    }

    // =========================
    // User Login
    // =========================
    public static void userLogin(Scanner input) {
        System.out.print("Enter email: ");
        String email = input.nextLine().trim();
        System.out.print("Enter password: ");
        String password = input.nextLine().trim();

        UserLoginService loginService = UserLoginService.getInstance();
        String loginResult = loginService.checkLogin(email, password);
        System.out.println(loginResult);

        if ("Login successful".equals(loginResult)) {
            userMode(input, email);
        }
    }

    // =========================
    // User Register
    // =========================
    public static void userRegister(Scanner input) {
        while (true) {
            System.out.print("Enter email: ");
            String email = input.nextLine().trim();
            System.out.println(isValidEmail(email));

            if (isValidEmail(email).contains("Invalid")) {
                continue;
            }

            System.out.print("Enter password: ");
            String password = input.nextLine().trim();

            UserRegistrationService regService =  UserRegistrationService.getInstance();
            String registerResult = regService.registerUser(email, password);
            System.out.println(registerResult);

            if (registerResult.contains("successfully")) {
                break;
            }
        }
    }

    // =========================
    // User Main Mode (After Login)
    // =========================
    public static void userMode(Scanner input, String email) {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. View all available slots");
            System.out.println("2. Book an appointment");
            System.out.println("3. Modify appointment");
            System.out.println("4. Cancel appointment");
            System.out.println("5. Back to main menu");
            System.out.print("Enter choice (1-5): ");

            int userChoice;
            try {
                userChoice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (userChoice) {
                case 1:
                    viewAllSlots();
                    break;

                case 2:
                    bookAppointmentFlow(input, email);
                    break;

                case 3:
                    modifyAppointmentFlow(input, email);
                    break;

                case 4:
                    System.out.println(cancelService.cancelAppointment(email));
                    break;

                case 5:
                    System.out.println("Returning to main menu...");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewAllSlots() {
        Schedule schedule = slots.loadSchedule();
        slots.viewAvailableSlots(schedule);
    }

    private static void bookAppointmentFlow(Scanner input, String email) {
        System.out.println("Choose appointment type:");
        for (AppointmentType type : AppointmentType.values()) {
            System.out.println(type.ordinal() + 1 + " → " + type);
        }

        try {
            int typeChoice = Integer.parseInt(input.nextLine().trim()) - 1;
            AppointmentType chosenType = AppointmentType.values()[typeChoice];

            System.out.print("Enter slot index to book: ");
            int slotIndex = Integer.parseInt(input.nextLine().trim());

            System.out.print("Enter number of participants: ");
            int participantsCount = Integer.parseInt(input.nextLine().trim());

            String result = bookingService.bookAppointment(slotIndex, email, chosenType, participantsCount);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void modifyAppointmentFlow(Scanner input, String email) {
        System.out.print("Enter new slot index: ");
        try {
            int newIndex = Integer.parseInt(input.nextLine().trim());

            System.out.println("Choose new appointment type:");
            for (AppointmentType type : AppointmentType.values()) {
                System.out.println(type.ordinal() + 1 + " → " + type);
            }
            int typeChoice = Integer.parseInt(input.nextLine().trim()) - 1;
            AppointmentType newType = AppointmentType.values()[typeChoice];

            String result = modifyService.modifyAppointment(email, newIndex, newType);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }
}
