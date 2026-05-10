package Persistence_Layer;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import Domain_Layer.*;
import Service_Layer.AppointmentService;
import Service_Layer.EmailService;
import presentation_Layer.Appointment;

public class AppointmentSlotsManager {
    private static final String SLOT_FILE = "schedule.txt";
    private static AppointmentSlotsManager instance;
    private final EmailService emailService = EmailService.getInstance();

    private AppointmentSlotsManager() {}

    public static AppointmentSlotsManager getInstance() {
        if (instance == null) {
            instance = new AppointmentSlotsManager();
        }
        return instance;
    }

    public Schedule buildSchedule(int lowHour, int highHour) {
        Schedule schedule = new Schedule();
        LocalDate today = LocalDate.now();

        for (int day = 1; day <= 7; day++) {
            LocalDate currentDay = today.plusDays(day);
            LocalDateTime start = currentDay.atTime(lowHour, 0);
            LocalDateTime end = currentDay.atTime(highHour, 0);

            while (start.isBefore(end)) {
                LocalDateTime slotEnd = start.plusMinutes(AppointmentService.MAX_DURATION_MINUTES);
                schedule.addSlot(new TimeSlot(start, slotEnd));
                start = slotEnd;
            }
        }
        saveSchedule(schedule);
        return schedule;
    }

    public void saveSchedule(Schedule schedule) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SLOT_FILE))) {
            for (TimeSlot slot : schedule.getSlots()) {
                writer.println(slot.getStart() + "," + slot.getEnd() + "," +
                        slot.isBooked() + "," +
                        slot.getBookedBy() + "," +
                        (slot.getType() == null ? "" : slot.getType().name()) + "," +
                        slot.getParticipants());
            }
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    // ↓↓↓ إعادة تنظيم ↓↓↓
    public Schedule loadSchedule() {
        File f = new File(SLOT_FILE);
        if (!f.exists()) {
            return buildScheduleIfConfigured();
        }
        return readScheduleFromFile(f);
    }

    private Schedule buildScheduleIfConfigured() {
        if (Appointment.selectedLow > 0) {
            return buildSchedule(Appointment.selectedLow, Appointment.selectedHigh);
        }
        return new Schedule();
    }

    private Schedule readScheduleFromFile(File file) {
        Schedule schedule = new Schedule();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                TimeSlot slot = parseSlot(sc.nextLine());
                if (slot != null) schedule.addSlot(slot);
            }
        } catch (Exception e) {
            System.out.println("Load error: " + e.getMessage());
        }
        return schedule;
    }

    private TimeSlot parseSlot(String line) {
        String[] parts = line.split(",");
        if (parts.length < 6) return null;

        LocalDateTime start = LocalDateTime.parse(parts[0]);
        LocalDateTime end = LocalDateTime.parse(parts[1]);
        boolean booked = Boolean.parseBoolean(parts[2]);
        String bookedBy = parts[3];
        AppointmentType type = parts[4].isEmpty() ? null : AppointmentType.valueOf(parts[4]);
        int participants = Integer.parseInt(parts[5]);

        TimeSlot slot = new TimeSlot(start, end);
        if (booked) slot.book(bookedBy, type, participants);
        return slot;
    }

    public void viewAvailableSlots(Schedule schedule) {
        System.out.println("\nAvailable Slots:");
        int i = 0;
        for (TimeSlot slot : schedule.getSlots()) {
            String state = slot.isBooked() ? "Booked" : "Free";
            String type = slot.getType() == null ? "-" : slot.getType().name();
            System.out.println(i++ + " → " + slot.getStart() +
                    " (" + state + ") Type: " + type +
                    " Participants: " + slot.getParticipants());
        }
    }

    // إرسال التذكيرات للمواعيد المحجوزة لليوم التالي
    public void sendReminders() {
        Schedule schedule = loadSchedule();
        LocalDateTime now = LocalDateTime.now();

        for (TimeSlot slot : schedule.getSlots()) {
            if (slot.isBooked() && slot.getStart().toLocalDate().equals(now.plusDays(1).toLocalDate())) {
                String email = slot.getBookedBy();
                if (email != null && !email.isEmpty()) {
                    NotificationMessage msg = new NotificationMessage(
                        email,
                        "Appointment Reminder",
                        "You have an appointment tomorrow at " + slot.getStart()
                    );
                    EmailService.run(msg);
                }
            }
        }
    }
}
