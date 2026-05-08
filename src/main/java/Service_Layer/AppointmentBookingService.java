package Service_Layer;

import Domain_Layer.AppointmentRules;
import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;

import java.time.LocalDateTime;

public class AppointmentBookingService {

    private final AppointmentSlotsManager slots = AppointmentSlotsManager.getInstance();
    private static AppointmentBookingService instance;

    private AppointmentBookingService() {}

    public static AppointmentBookingService getInstance() {
        if (instance == null) {
            instance = new AppointmentBookingService();
        }
        return instance;
    }

    public String bookAppointment(int index, String email, AppointmentType type, int participantsCount) {
        // حمّل الجدول من الملف
        Schedule schedule = slots.loadSchedule();

        if (index < 0 || index >= schedule.getSlots().size()) return "Invalid index.";

        TimeSlot slot = schedule.getSlots().get(index);

        if (slot.getStart().isBefore(LocalDateTime.now())) return "Cannot book past appointments.";
        if (slot.isBooked()) return "Already booked.";

        // تحقق إذا المستخدم عنده حجز مسبق
        for (TimeSlot s : schedule.getSlots()) {
            if (s.isBooked() && email.equals(s.getBookedBy())) {
                return "User already has a booking.";
            }
        }

        // تحقق من عدد المشاركين
        if (!AppointmentRules.validateParticipants(type, participantsCount)) {
            return "Invalid participants for " + type;
        }

        // نفذ الحجز
        slot.book(email, type, participantsCount);

        // احفظ الجدول بعد التعديل
        slots.saveSchedule(schedule);

        return "Booked successfully with " + participantsCount + " participants.";
    }
}
