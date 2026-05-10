package Persistence_Layer;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.logging.Logger;

public class AdminTimeConfig {
    private static final String CONFIG_FILE = "admin_time_range.txt";
    private static AdminTimeConfig instance;
    private static final Logger logger = Logger.getLogger(AdminTimeConfig.class.getName());

    private AdminTimeConfig() {}

    public static AdminTimeConfig getInstance() {
        if (instance == null) {
            instance = new AdminTimeConfig();
        }
        return instance;
    }

    public int[] loadCurrentRangeAndDuration() {
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
            logger.severe("Error loading config: " + e.getMessage());
        }
        return null;
    }

    public void saveRangeAndDuration(int low, int high, int duration) {
        int week = LocalDate.now()
                .get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_FILE))) {
            writer.println(week + "," + low + "," + high + "," + duration);
        } catch (IOException e) {
            logger.warning("Could not save admin config: " + e.getMessage());
        }
    }
}
