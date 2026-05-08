package Service_Layer;

import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;

public class AppointmentCancellationService {

    private final AppointmentSlotsManager slots = AppointmentSlotsManager.getInstance();
    private static AppointmentCancellationService instance;

    private AppointmentCancellationService() {}

    public static AppointmentCancellationService getInstance() {
        if (instance == null) {
            instance = new AppointmentCancellationService();
        }
        return instance;
    }

    // إلغاء الموعد بواسطة المستخدم (حسب الإيميل)
    public String cancelAppointment(String email) {
        Schedule schedule = slots.loadSchedule();
        for (TimeSlot slot : schedule.getSlots()) {
            if (slot.isBooked() && slot.getBookedBy().equals(email)) {
                slot.cancel();
                slots.saveSchedule(schedule);
                return "Cancelled successfully.";
            }
        }
        return "No booking found for this user.";
    }

    // إلغاء الموعد بواسطة الأدمن (حسب الفهرس)
    public String cancelAppointmentByAdmin(int index) {
        Schedule schedule = slots.loadSchedule();

        if (index < 0 || index >= schedule.getSlots().size()) return "Invalid index.";
        TimeSlot slot = schedule.getSlots().get(index);

        if (!slot.isBooked()) return "Slot is already free.";

        slot.cancel();
        slots.saveSchedule(schedule);
        return "Cancelled by admin.";
    }
}
