package Service_Layer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;
import Domain_Layer.AppointmentRules;
import Domain_Layer.AppointmentType;
import Domain_Layer.TimeSlot;

public class AppointmentService {

    public static int MAX_DURATION_MINUTES = 30;
    private static AppointmentService instance;

    // كونستركتور خاص → يمنع إنشاء كائنات جديدة من الخارج
    private AppointmentService() {
        // أي تهيئة أولية
    }

    // دالة الوصول الوحيدة للنسخة
    public static AppointmentService getInstance() {
        if (instance == null) {
            instance = new AppointmentService();
        }
        return instance;
    }

    // يفضل جعلها public static nested class حتى يمكن التقاطها من أماكن أخرى
    public static class InvalidDurationException extends RuntimeException {
        public InvalidDurationException(String message) {
            super(message);
        }
    }

    /**
     * يتحقق من صحة الموعد حسب القواعد والنوع
     */
    public String createAppointment(TimeSlot slot, AppointmentType type, int participants) {
        long duration = slot.getDurationMinutes();

        int allowedDuration = AppointmentRules.getMaxDuration(type);
        if (duration > allowedDuration) {
            throw new InvalidDurationException(
                "This appointment type (" + type + ") allows maximum " + allowedDuration + " minutes only."
            );
        }

        if (!AppointmentRules.validateParticipants(type, participants)) {
            return "Invalid number of participants for type: " + type;
        }

        return "Appointment booked successfully for type: " + type + 
               " (" + duration + " minutes, " + participants + " participants)";
    }

    /**
     * اختيار مدة الموعد (يُستدعى مرة واحدة عند إعداد الأسبوع)
     */
    public void chooseAppointmentDuration(Scanner input) {   // ← تم تصليح الاسم

        System.out.println("\nSelect appointment duration:");
        System.out.println("1 → 15 minutes");
        System.out.println("2 → 30 minutes");
        System.out.println("3 → 45 minutes");
        System.out.println("4 → 60 minutes");

        String durChoice = input.nextLine().trim();

        try {
            int opt = Integer.parseInt(durChoice);
            switch (opt) {
                case 1:  MAX_DURATION_MINUTES = 15; break;
                case 2:  MAX_DURATION_MINUTES = 30; break;
                case 3:  MAX_DURATION_MINUTES = 45; break;
                case 4:  MAX_DURATION_MINUTES = 60; break;
                default:
                    System.out.println("Invalid choice → using default 30 minutes");
                    MAX_DURATION_MINUTES = 30;
            }
        } catch (Exception e) {
            System.out.println("Invalid input → using default 30 minutes");
            MAX_DURATION_MINUTES = 30;
        }

        System.out.println("Appointment duration has been set to: " + 
                          MAX_DURATION_MINUTES + " minutes");
    }
}