package presentation_Layer;

import java.util.Scanner;
import Domain_Layer.Schedule;
import Persistence_Layer.AppointmentSlotsManager;
import Service_Layer.AppointmentCancellationService;
import Service_Layer.AppointmentModificationService;
import Service_Layer.AdministratorLogin;

/**
 * AdminUI - Presentation Layer
 */
public class AdminUI {

    private static final AppointmentCancellationService cancelService = AppointmentCancellationService.getInstance();
    private static final AppointmentModificationService modifyService = AppointmentModificationService.getInstance();
    private static final AppointmentSlotsManager slots = AppointmentSlotsManager.getInstance();

    public static void adminLogin(Scanner input) {
        System.out.print("Enter admin email: ");
        String email = input.nextLine().trim();
        System.out.print("Enter password: ");
        String password = input.nextLine().trim();

        String loginResult = AdministratorLogin.checkLogin(email, password, null);
        System.out.println(loginResult);

        if ("Login successful".equals(loginResult)) {
            adminMenu(input);
        }
    }

    public static void adminMenu(Scanner input) {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Modify appointment (by admin)");
            System.out.println("2. Cancel appointment (by admin)");
            System.out.println("3. View all slots");
            System.out.println("4. Back to main menu");
            System.out.print("Enter choice (1-4): ");

            int choice;
            try {
                choice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    modifyByAdmin(input);
                    break;
                case 2:
                    cancelByAdmin(input);
                    break;
                case 3:
                    viewAllSlots();
                    break;
                case 4:
                    System.out.println("Returning to main menu...");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void modifyByAdmin(Scanner input) {
        try {
            System.out.print("Enter old slot index: ");
            int oldIndex = Integer.parseInt(input.nextLine().trim());
            System.out.print("Enter new slot index: ");
            int newIndex = Integer.parseInt(input.nextLine().trim());

            String result = modifyService.modifyAppointmentByAdmin(oldIndex, newIndex);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void cancelByAdmin(Scanner input) {
        try {
            System.out.print("Enter slot index to cancel: ");
            int index = Integer.parseInt(input.nextLine().trim());

            String result = cancelService.cancelAppointmentByAdmin(index);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("Invalid input: " + e.getMessage());
        }
    }

    private static void viewAllSlots() {
        Schedule schedule = slots.loadSchedule();
        slots.viewAvailableSlots(schedule);
    }
}
