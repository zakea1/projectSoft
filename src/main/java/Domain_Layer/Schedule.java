package Domain_Layer;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private List<TimeSlot> slots = new ArrayList<>();

    public void addSlot(TimeSlot slot) {
        slots.add(slot);
    }

    public List<TimeSlot> getSlots() {
        return slots;
    }

    public List<TimeSlot> getAvailableSlots() {
        return slots.stream().filter(s -> !s.isBooked()).toList();
    }
}
