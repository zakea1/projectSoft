package Service_Layer;

import java.time.LocalDateTime;

import Domain_Layer.AppointmentRules;
import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;

/**
 * Service class responsible for managing appointment operations
 * such as booking, cancelling, and modifying appointments.
 * <p>
 * This class follows the Singleton design pattern and interacts
 * with the persistence layer through {@code AppointmentSlotsManager}.
 */
public class AppointmentSlots {

    /**
     * Singleton instance of the persistence manager.
     */
    private final AppointmentSlotsManager slots = AppointmentSlotsManager.getInstance();

    /**
     * Singleton instance of AppointmentSlots.
     */
    private static AppointmentSlots instance;

    /** Message returned when an invalid index is used. */
    private static final String INVALID_INDEX_MSG = "Invalid index.";

    /** Message returned when no booking exists. */
    private static final String NO_BOOKING_MSG = "No booking found.";

    /** Message returned when a slot is already booked. */
    private static final String ALREADY_BOOKED_MSG = "Already booked.";

    /** Message returned after successful cancellation. */
    private static final String CANCELLED_MSG = "Cancelled.";

    /** Message returned after admin cancellation. */
    private static final String CANCELLED_BY_ADMIN_MSG = "Cancelled by admin.";

    /** Message returned when the old slot is not booked. */
    private static final String OLD_SLOT_NOT_BOOKED_MSG = "Old slot not booked.";

    /** Message returned when the new slot is already booked. */
    private static final String NEW_SLOT_ALREADY_BOOKED_MSG = "New slot already booked.";

    /**
     * Private constructor to prevent external instantiation.
     */
    private AppointmentSlots() {}

    /**
     * Returns the singleton instance of AppointmentSlots.
     *
     * @return the AppointmentSlots instance
     */
    public static AppointmentSlots getInstance() {
        if (instance == null) {
            instance = new AppointmentSlots();
        }
        return instance;
    }

    /**
     * Loads the current schedule from persistence storage.
     *
     * @return the loaded schedule
     */
    private Schedule getSchedule() {
        return slots.loadSchedule();
    }

    /**
     * Saves the updated schedule to persistence storage.
     *
     * @param schedule the schedule to save
     */
    private void saveSchedule(Schedule schedule) {
        slots.saveSchedule(schedule);
    }

    /**
     * Checks whether a slot index is valid.
     *
     * @param schedule the schedule containing slots
     * @param index the slot index
     * @return true if index is valid, otherwise false
     */
    private boolean isValidIndex(Schedule schedule, int index) {
        return index >= 0 && index < schedule.getSlots().size();
    }

    /**
     * Retrieves a specific slot from the schedule.
     *
     * @param schedule the schedule containing slots
     * @param index the slot index
     * @return the requested time slot
     */
    private TimeSlot getSlot(Schedule schedule, int index) {
        return schedule.getSlots().get(index);
    }

    /**
     * Finds the booking associated with a specific user email.
     *
     * @param schedule the schedule to search in
     * @param email the user's email
     * @return the booked slot if found, otherwise null
     */
    private TimeSlot findUserBooking(Schedule schedule, String email) {
        for (TimeSlot slot : schedule.getSlots()) {
            if (slot.isBooked() && slot.getBookedBy().equals(email)) {
                return slot;
            }
        }
        return null;
    }

    /**
     * Books an appointment for a user.
     *
     * @param index the slot index
     * @param email the user's email
     * @param type the appointment type
     * @param participantsCount number of participants
     * @return booking result message
     */
    public String bookAppointment(int index, String email,
                                  AppointmentType type,
                                  int participantsCount) {

        Schedule schedule = getSchedule();

        if (!isValidIndex(schedule, index))
            return INVALID_INDEX_MSG;

        TimeSlot slot = getSlot(schedule, index);

        if (slot.getStart().isBefore(LocalDateTime.now()))
            return "Cannot book past appointments.";

        if (slot.isBooked())
            return ALREADY_BOOKED_MSG;

        if (findUserBooking(schedule, email) != null)
            return "User already has a booking.";

        if (!AppointmentRules.validateParticipants(type, participantsCount))
            return "Invalid participants for " + type;

        slot.book(email, type, participantsCount);
        saveSchedule(schedule);

        return "Booked successfully with "
                + participantsCount + " participants.";
    }

    /**
     * Cancels a user's appointment.
     *
     * @param email the user's email
     * @return cancellation result message
     */
    public String cancelAppointment(String email) {

        Schedule schedule = getSchedule();
        TimeSlot slot = findUserBooking(schedule, email);

        if (slot == null)
            return NO_BOOKING_MSG;

        slot.cancel();
        saveSchedule(schedule);

        return CANCELLED_MSG;
    }

    /**
     * Cancels an appointment by an administrator.
     *
     * @param index the slot index
     * @return cancellation result message
     */
    public String cancelAppointmentByAdmin(int index) {

        Schedule schedule = getSchedule();

        if (!isValidIndex(schedule, index))
            return INVALID_INDEX_MSG;

        TimeSlot slot = getSlot(schedule, index);

        if (!slot.isBooked())
            return "Already free.";

        slot.cancel();
        saveSchedule(schedule);

        return CANCELLED_BY_ADMIN_MSG;
    }

    /**
     * Modifies a user's appointment to a new slot and type.
     *
     * @param email the user's email
     * @param newIndex the new slot index
     * @param newType the new appointment type
     * @return modification result message
     */
    public String modifyAppointment(String email,
                                    int newIndex,
                                    AppointmentType newType) {

        Schedule schedule = getSchedule();

        if (!isValidIndex(schedule, newIndex))
            return INVALID_INDEX_MSG;

        TimeSlot oldSlot = findUserBooking(schedule, email);

        if (oldSlot == null)
            return NO_BOOKING_MSG;

        TimeSlot newSlot = getSlot(schedule, newIndex);

        if (newSlot.isBooked())
            return NEW_SLOT_ALREADY_BOOKED_MSG;

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

    /**
     * Modifies an appointment by an administrator.
     *
     * @param oldIndex the current booked slot index
     * @param newIndex the new slot index
     * @return modification result message
     */
    public String modifyAppointmentByAdmin(int oldIndex, int newIndex) {

        Schedule schedule = getSchedule();

        if (!isValidIndex(schedule, oldIndex)
                || !isValidIndex(schedule, newIndex)) {

            return INVALID_INDEX_MSG;
        }

        TimeSlot oldSlot = getSlot(schedule, oldIndex);
        TimeSlot newSlot = getSlot(schedule, newIndex);

        if (!oldSlot.isBooked())
            return OLD_SLOT_NOT_BOOKED_MSG;

        if (newSlot.isBooked())
            return NEW_SLOT_ALREADY_BOOKED_MSG;

        String email = oldSlot.getBookedBy();
        AppointmentType type = oldSlot.getType();
        int participantsCount = oldSlot.getParticipants();

        oldSlot.cancel();
        newSlot.book(email, type, participantsCount);

        saveSchedule(schedule);

        return "Modified by admin.";
    }
}
