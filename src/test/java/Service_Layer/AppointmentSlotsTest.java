package Service_Layer;

import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentSlotsTest {

    private AppointmentSlots service;

    @BeforeEach
    void setUp() {

        service = AppointmentSlots.getInstance();

        Schedule schedule = new Schedule();

        List<TimeSlot> slots = new ArrayList<>();

        // FIX 1: TimeSlot needs (start, end)
        for (int i = 0; i < 5; i++) {

            LocalDateTime start = LocalDateTime.now().plusDays(1 + i);
            LocalDateTime end = start.plusHours(1);

            TimeSlot slot = new TimeSlot(start, end);

            slots.add(slot);
        }

        // FIX 2: Schedule likely uses addSlot instead of setSlots
        for (TimeSlot slot : slots) {
            schedule.addSlot(slot);
        }

        AppointmentSlotsManager.getInstance().saveSchedule(schedule);
    }

    @Test
    void testBookAppointmentSuccess() {

        String result = service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals("Booked successfully with 1 participants.", result);
    }

    @Test
    void testBookInvalidIndex() {

        String result = service.bookAppointment(
                999,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals("Invalid index.", result);
    }

    @Test
    void testCancelAppointmentSuccess() {

        service.bookAppointment(0, "a@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.cancelAppointment("a@mail.com");

        assertEquals("Cancelled.", result);
    }

    @Test
    void testModifyAppointmentSuccess() {

        service.bookAppointment(0, "m@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.modifyAppointment(
                "m@mail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("Modified successfully.", result);
    }

    @Test
    void testAdminModifySuccess() {

        service.bookAppointment(0, "admin@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.modifyAppointmentByAdmin(0, 1);

        assertEquals("Modified by admin.", result);
    }
}
