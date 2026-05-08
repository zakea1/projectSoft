package presentation_Layer;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Domain_Layer.Schedule;
import Persistence_Layer.AdminTimeConfig;
import Persistence_Layer.AppointmentSlotsManager;
import Service_Layer.AdministratorLogin;
import Service_Layer.AppointmentService;
import Service_Layer.EmailService;

public class Appointment {

    public static int selectedLow = -1;
    public static int selectedHigh = -1;

    static AppointmentService service = AppointmentService.getInstance();
    public static AppointmentSlotsManager slots = AppointmentSlotsManager.getInstance();
    static AdministratorLogin login = AdministratorLogin.getInstance();
    static EmailService emailService = EmailService.getInstance();
    public static AdminTimeConfig adminTimeConfig = AdminTimeConfig.getInstance();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Schedule schedule;

        int[] savedConfig = adminTimeConfig.loadCurrentRangeAndDuration();
        if (savedConfig != null) {
            selectedLow = savedConfig[0];
            selectedHigh = savedConfig[1];
            AppointmentService.MAX_DURATION_MINUTES = savedConfig[2];
            System.out.println("\nUsing the previously saved time range and duration for this week:");
            System.out.println("From " + selectedLow + ":00 to " + selectedHigh + ":00, Duration: "
                               + AppointmentService.MAX_DURATION_MINUTES + " minutes");
            schedule = slots.loadSchedule();   
        } else {
            boolean valid = false;
            schedule = new Schedule();
            while (!valid) {
                System.out.println("\nSelect the appointment time range for this week (once only):");
                System.out.println("1 → 8:00 AM - 2:00 PM");
                System.out.println("2 → 9:00 AM - 3:00 PM");
                System.out.println("3 → 10:00 AM - 4:00 PM");
                System.out.println("4 → 11:00 AM - 5:00 PM");

                String choiceStr = input.nextLine().trim();
                try {
                    int option = Integer.parseInt(choiceStr);
                    if (option >= 1 && option <= 4) {
                        selectedLow = option + 7;
                        selectedHigh = selectedLow + 6;
                        valid = true;

                        service.chooseAppointmentDuration(input);
                        adminTimeConfig.saveRangeAndDuration(selectedLow, selectedHigh,
                                                             AppointmentService.MAX_DURATION_MINUTES);
                        schedule = slots.buildSchedule(selectedLow, selectedHigh); // ← يبني جدول جديد
                    } else {
                        System.out.println("Please enter a number between 1 and 4.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                }
            }
        }

                slots.viewAvailableSlots(schedule);

  
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> slots.sendReminders(), 0, 1, TimeUnit.DAYS);

       
        while (true) {
            System.out.println("\nWelcome to Appointment Scheduling System");
            System.out.println("1. Administrator Login");
            System.out.println("2. User Login/Register");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(input.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (choice == 1) {
                AdminUI.adminLogin(input);
            } else if (choice == 2) {
                UserUI.userAccountMenu(input);
            } else if (choice == 3) {
                System.out.println("Thank you! Goodbye.");
                scheduler.shutdown();
                break;
            } else {
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
        input.close();
    }
}
