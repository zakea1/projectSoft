package Service_Layer;

import Domain_Layer.AppointmentRules;
import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;

public class AppointmentModificationService {

    private final AppointmentSlotsManager slots = AppointmentSlotsManager.getInstance();
    private static AppointmentModificationService instance;

    private AppointmentModificationService() {}

    public static AppointmentModificationService getInstance() {
        if (instance == null) {
            instance = new AppointmentModificationService();
        }
        return instance;
    }

    // تعديل موعد بواسطة المستخدم
    public String modifyAppointment(String email, int newIndex, AppointmentType newType) {
        Schedule schedule = slots.loadSchedule();

        if (newIndex < 0 || newIndex >= schedule.getSlots().size()) return "Invalid index.";

        for (TimeSlot slot : schedule.getSlots()) {
            if (slot.isBooked() && slot.getBookedBy().equals(email)) {
                TimeSlot newSlot = schedule.getSlots().get(newIndex);

                if (newSlot.isBooked()) return "New slot already booked.";
                if (!AppointmentRules.validateParticipants(newType, slot.getParticipants())) {
                    return "Invalid participants for " + newType;
                }

                // إلغاء القديم
                slot.cancel();

                // حجز الجديد بنفس عدد المشاركين
                newSlot.book(email, newType, slot.getParticipants());

                slots.saveSchedule(schedule);
                return "Modified successfully.";
            }
        }
        return "No booking found for this user.";
    }

    // تعديل موعد بواسطة الأدمن
    public String modifyAppointmentByAdmin(int oldIndex, int newIndex) {
        Schedule schedule = slots.loadSchedule();

        if (oldIndex < 0 || newIndex < 0 ||
            oldIndex >= schedule.getSlots().size() || newIndex >= schedule.getSlots().size())
            return "Invalid index.";

        TimeSlot oldSlot = schedule.getSlots().get(oldIndex);
        TimeSlot newSlot = schedule.getSlots().get(newIndex);

        if (!oldSlot.isBooked()) return "Old slot not booked.";
        if (newSlot.isBooked()) return "New slot already booked.";

        String email = oldSlot.getBookedBy();
        AppointmentType type = oldSlot.getType();
        int participantsCount = oldSlot.getParticipants();

        // إلغاء القديم
        oldSlot.cancel();

        // حجز الجديد بنفس البيانات
        newSlot.book(email, type, participantsCount);

        slots.saveSchedule(schedule);
        return "Modified by admin.";
    }
}
