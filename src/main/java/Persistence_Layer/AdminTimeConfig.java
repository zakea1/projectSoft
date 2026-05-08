package Persistence_Layer;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * AdminTimeConfig class
 * ---------------------
 * Handles saving and loading of admin-defined time ranges and durations
 * for appointments. The configuration is stored in a simple text file
 * ("admin_time_range.txt") and is tied to the current week number.
 *
 * Core functionalities:
 * - Load the saved time range and duration for the current week.
 * - Save a new time range and duration for the current week.
 *
 * Notes:
 * - Data is stored as plain text in the format:
 *   weekNumber,low,high,duration
 * - If the saved week does not match the current week, the configuration
 *   is considered invalid and returns null.
 */
public class AdminTimeConfig {
    private static final String CONFIG_FILE = "admin_time_range.txt";
    private static AdminTimeConfig instance;

    // كونستركتور خاص → يمنع إنشاء كائنات جديدة من الخارج
    private AdminTimeConfig() {
        // أي تهيئة أولية
    }

    // دالة الوصول الوحيدة للنسخة
    public static AdminTimeConfig getInstance() {
        if (instance == null) {
            instance = new AdminTimeConfig();
        }
        return instance;
    }
    public  int[] loadCurrentRangeAndDuration() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line == null) return null;

            String[] parts = line.split(",");
            if (parts.length != 4) return null; // week, low, high, duration

            int savedWeek = Integer.parseInt(parts[0]);
            int low = Integer.parseInt(parts[1]);
            int high = Integer.parseInt(parts[2]);
            int duration = Integer.parseInt(parts[3]);

            int currentWeek = LocalDate.now()
                    .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

            if (savedWeek == currentWeek) {
                return new int[]{low, high, duration};
            }
        } catch (Exception e) {
            System.out.println("Error loading config");
        }
        return null;
    }

    public  void saveRangeAndDuration(int low, int high, int duration) {
        int week = LocalDate.now()
                .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            writer.println(week + "," + low + "," + high + "," + duration);
        } catch (IOException e) {
            System.out.println("Warning: Could not save admin config");
        }
    }
}