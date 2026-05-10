package Service_Layer;

import java.time.LocalDateTime;

import Domain_Layer.AppointmentRules;
import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;

public class AppointmentSlots {

    private final AppointmentSlotsManager slots =
            AppointmentSlotsManager.getInstance();

    private static AppointmentSlots instance;

    private AppointmentSlots() {}

    public static AppointmentSlots getInstance() {
        if (instance == null) {
            instance = new AppointmentSlots();
        }
        return instance;
    }

    // =========================
    // Helper Methods
    // =========================

    private Schedule getSchedule() {
        return slots.loadSchedule();
    }

    private void saveSchedule(Schedule schedule) {
        slots.saveSchedule(schedule);
    }

    private boolean isValidIndex(Schedule schedule, int index) {
        return index >= 0 && index < schedule.getSlots().size();
    }

    private TimeSlot getSlot(Schedule schedule, int index) {
        return schedule.getSlots().get(index);
    }

    private TimeSlot findUserBooking(Schedule schedule, String email) {

        for (TimeSlot slot : schedule.getSlots()) {

            if (slot.isBooked()
                    && slot.getBookedBy().equals(email)) {

                return slot;
            }
        }

        return null;
    }

    // =========================
    // BOOK APPOINTMENT
    // =========================

    public String bookAppointment(int index,
                                  String email,
                                  AppointmentType type,
                                  int participantsCount) {

        Schedule schedule = getSchedule();

        if (!isValidIndex(schedule, index)) {
            return "Invalid index.";
        }

        TimeSlot slot = getSlot(schedule, index);

        if (slot.getStart().isBefore(LocalDateTime.now())) {
            return "Cannot book past appointments.";
        }

        if (slot.isBooked()) {
            return "Already booked.";
        }

        if (findUserBooking(schedule, email) != null) {
            return "User already has a booking.";
        }

        if (!AppointmentRules.validateParticipants(type, participantsCount)) {
            return "Invalid participants for " + type;
        }

        slot.book(email, type, participantsCount);

        saveSchedule(schedule);

        return "Booked successfully with "
                + participantsCount
                + " participants.";
    }

    // =========================
    // CANCEL USER APPOINTMENT
    // =========================

    public String cancelAppointment(String email) {

        Schedule schedule = getSchedule();

        TimeSlot slot = findUserBooking(schedule, email);

        if (slot == null) {
            return "No booking found.";
        }

        slot.cancel();

        saveSchedule(schedule);

        return "Cancelled.";
    }

    // =========================
    // CANCEL BY ADMIN
    // =========================

    public String cancelAppointmentByAdmin(int index) {

        Schedule schedule = getSchedule();

        if (!isValidIndex(schedule, index)) {
            return "Invalid index.";
        }

        TimeSlot slot = getSlot(schedule, index);

        if (!slot.isBooked()) {
            return "Already free.";
        }

        slot.cancel();

        saveSchedule(schedule);

        return "Cancelled by admin.";
    }

    // =========================
    // MODIFY USER APPOINTMENT
    // =========================

    public String modifyAppointment(String email,
                                    int newIndex,
                                    AppointmentType newType) {

        Schedule schedule = getSchedule();

        if (!isValidIndex(schedule, newIndex)) {
            return "Invalid index.";
        }

        TimeSlot oldSlot = findUserBooking(schedule, email);

        if (oldSlot == null) {
            return "No booking found.";
        }

        TimeSlot newSlot = getSlot(schedule, newIndex);

        if (newSlot.isBooked()) {
            return "New slot already booked.";
        }

        if (!AppointmentRules.validateParticipants(
                newType,
                oldSlot.getParticipants())) {

            return "Invalid participants for " + newType;
        }

        int participants = oldSlot.getParticipants();

        oldSlot.cancel();

        newSlot.book(email, newType, participants);

        saveSchedule(schedule);

        return "Modified successfully.";
    }

    // =========================
    // MODIFY BY ADMIN
    // =========================

    public String modifyAppointmentByAdmin(int oldIndex,
                                           int newIndex) {

        Schedule schedule = getSchedule();

        if (!isValidIndex(schedule, oldIndex)
                || !isValidIndex(schedule, newIndex)) {

            return "Invalid index.";
        }

        TimeSlot oldSlot = getSlot(schedule, oldIndex);

        TimeSlot newSlot = getSlot(schedule, newIndex);

        if (!oldSlot.isBooked()) {
            return "Old slot not booked.";
        }

        if (newSlot.isBooked()) {
            return "New slot already booked.";
        }

        String email = oldSlot.getBookedBy();

        AppointmentType type = oldSlot.getType();

        int participantsCount = oldSlot.getParticipants();

        oldSlot.cancel();

        newSlot.book(email, type, participantsCount);

        saveSchedule(schedule);

        return "Modified by admin.";
    }
}
