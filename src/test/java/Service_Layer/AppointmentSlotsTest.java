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

        // Reset schedule manually (important for singleton state)
        Schedule schedule = new Schedule();

        List<TimeSlot> slots = new ArrayList<>();

        // Create future slots for valid booking
        for (int i = 0; i < 5; i++) {
            TimeSlot slot = new TimeSlot(
                    LocalDateTime.now().plusDays(1 + i)
            );
            slots.add(slot);
        }

        schedule.setSlots(slots);

        AppointmentSlotsManager.getInstance().saveSchedule(schedule);
    }

    // =========================
    // BOOKING TESTS
    // =========================

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
    void testDoubleBookingSameSlot() {

        service.bookAppointment(0, "a@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.bookAppointment(
                0,
                "b@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals("Already booked.", result);
    }

    @Test
    void testUserAlreadyHasBooking() {

        service.bookAppointment(0, "same@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.bookAppointment(
                1,
                "same@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals("User already has a booking.", result);
    }

    @Test
    void testInvalidParticipants() {

        String result = service.bookAppointment(
                1,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                999
        );

        assertTrue(result.startsWith("Invalid participants"));
    }

    // =========================
    // CANCEL TESTS
    // =========================

    @Test
    void testCancelAppointmentSuccess() {

        service.bookAppointment(0, "cancel@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.cancelAppointment("cancel@mail.com");

        assertEquals("Cancelled.", result);
    }

    @Test
    void testCancelAppointmentNotFound() {

        String result = service.cancelAppointment("notfound@mail.com");

        assertEquals("No booking found.", result);
    }

    @Test
    void testAdminCancelSuccess() {

        service.bookAppointment(0, "admin@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.cancelAppointmentByAdmin(0);

        assertEquals("Cancelled by admin.", result);
    }

    @Test
    void testAdminCancelInvalidIndex() {

        String result = service.cancelAppointmentByAdmin(999);

        assertEquals("Invalid index.", result);
    }

    // =========================
    // MODIFY TESTS (USER)
    // =========================

    @Test
    void testModifyAppointmentSuccess() {

        service.bookAppointment(0, "mod@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.modifyAppointment(
                "mod@mail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("Modified successfully.", result);
    }

    @Test
    void testModifyNoBookingFound() {

        String result = service.modifyAppointment(
                "ghost@mail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("No booking found.", result);
    }

    @Test
    void testModifyInvalidIndex() {

        service.bookAppointment(0, "mod2@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.modifyAppointment(
                "mod2@mail.com",
                999,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("Invalid index.", result);
    }

    @Test
    void testModifyToBookedSlot() {

        service.bookAppointment(0, "a@mail.com", AppointmentType.INDIVIDUAL, 1);
        service.bookAppointment(1, "b@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.modifyAppointment(
                "a@mail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("New slot already booked.", result);
    }

    // =========================
    // MODIFY TESTS (ADMIN)
    // =========================

    @Test
    void testAdminModifySuccess() {

        service.bookAppointment(0, "adminmod@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.modifyAppointmentByAdmin(0, 1);

        assertEquals("Modified by admin.", result);
    }

    @Test
    void testAdminModifyInvalidOldIndex() {

        String result = service.modifyAppointmentByAdmin(999, 1);

        assertEquals("Invalid index.", result);
    }

    @Test
    void testAdminModifyOldNotBooked() {

        String result = service.modifyAppointmentByAdmin(0, 1);

        assertEquals("Old slot not booked.", result);
    }

    @Test
    void testAdminModifyNewAlreadyBooked() {

        service.bookAppointment(0, "a@mail.com", AppointmentType.INDIVIDUAL, 1);
        service.bookAppointment(1, "b@mail.com", AppointmentType.INDIVIDUAL, 1);

        String result = service.modifyAppointmentByAdmin(0, 1);

        assertEquals("New slot already booked.", result);
    }
}
