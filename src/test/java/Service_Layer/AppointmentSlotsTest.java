package Service_Layer;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;

class AppointmentSlotsTest {

    private AppointmentSlots service;

    @BeforeEach
    void setUp() {

        service = AppointmentSlots.getInstance();

        Schedule schedule = new Schedule();

        // future free slot
        schedule.getSlots().add(
                new TimeSlot(LocalDateTime.now().plusDays(1))
        );

        // another future free slot
        schedule.getSlots().add(
                new TimeSlot(LocalDateTime.now().plusDays(2))
        );

        // past slot
        schedule.getSlots().add(
                new TimeSlot(LocalDateTime.now().minusDays(1))
        );

        AppointmentSlotsManager
                .getInstance()
                .saveSchedule(schedule);
    }

    // =========================
    // BOOK APPOINTMENT
    // =========================

    @Test
    void testBookAppointmentSuccess() {

        String result = service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertTrue(result.contains("Booked successfully"));
    }

    @Test
    void testBookAppointmentInvalidIndex() {

        String result = service.bookAppointment(
                -1,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals("Invalid index.", result);
    }

    @Test
    void testBookPastAppointment() {

        String result = service.bookAppointment(
                2,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals(
                "Cannot book past appointments.",
                result
        );
    }

    @Test
    void testBookAlreadyBooked() {

        service.bookAppointment(
                0,
                "first@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        String result = service.bookAppointment(
                0,
                "second@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals("Already booked.", result);
    }

    @Test
    void testUserAlreadyHasBooking() {

        service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        String result = service.bookAppointment(
                1,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals(
                "User already has a booking.",
                result
        );
    }

    @Test
    void testInvalidParticipants() {

        String result = service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                5
        );

        assertTrue(
                result.contains("Invalid participants")
        );
    }

    // =========================
    // CANCEL APPOINTMENT
    // =========================

    @Test
    void testCancelAppointmentSuccess() {

        service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        String result =
                service.cancelAppointment("test@mail.com");

        assertEquals("Cancelled.", result);
    }

    @Test
    void testCancelAppointmentNoBooking() {

        String result =
                service.cancelAppointment("none@mail.com");

        assertEquals("No booking found.", result);
    }

    // =========================
    // CANCEL BY ADMIN
    // =========================

    @Test
    void testCancelByAdminSuccess() {

        service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        String result =
                service.cancelAppointmentByAdmin(0);

        assertEquals("Cancelled by admin.", result);
    }

    @Test
    void testCancelByAdminInvalidIndex() {

        String result =
                service.cancelAppointmentByAdmin(99);

        assertEquals("Invalid index.", result);
    }

    @Test
    void testCancelByAdminAlreadyFree() {

        String result =
                service.cancelAppointmentByAdmin(0);

        assertEquals("Already free.", result);
    }

    // =========================
    // MODIFY APPOINTMENT
    // =========================

    @Test
    void testModifyAppointmentSuccess() {

        service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        String result = service.modifyAppointment(
                "test@mail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals(
                "Modified successfully.",
                result
        );
    }

    @Test
    void testModifyAppointmentInvalidIndex() {

        String result = service.modifyAppointment(
                "test@mail.com",
                99,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("Invalid index.", result);
    }

    @Test
    void testModifyAppointmentNoBooking() {

        String result = service.modifyAppointment(
                "none@mail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("No booking found.", result);
    }

    @Test
    void testModifyAppointmentNewSlotBooked() {

        service.bookAppointment(
                0,
                "first@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        service.bookAppointment(
                1,
                "second@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        String result = service.modifyAppointment(
                "first@mail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals(
                "New slot already booked.",
                result
        );
    }

    @Test
    void testModifyAppointmentInvalidParticipants() {

        service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.GROUP,
                5
        );

        String result = service.modifyAppointment(
                "test@mail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertTrue(
                result.contains("Invalid participants")
        );
    }

    // =========================
    // MODIFY BY ADMIN
    // =========================

    @Test
    void testModifyByAdminSuccess() {

        service.bookAppointment(
                0,
                "test@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        String result =
                service.modifyAppointmentByAdmin(0, 1);

        assertEquals("Modified by admin.", result);
    }

    @Test
    void testModifyByAdminInvalidIndex() {

        String result =
                service.modifyAppointmentByAdmin(0, 99);

        assertEquals("Invalid index.", result);
    }

    @Test
    void testModifyByAdminOldSlotNotBooked() {

        String result =
                service.modifyAppointmentByAdmin(0, 1);

        assertEquals("Old slot not booked.", result);
    }

    @Test
    void testModifyByAdminNewSlotBooked() {

        service.bookAppointment(
                0,
                "first@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        service.bookAppointment(
                1,
                "second@mail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        String result =
                service.modifyAppointmentByAdmin(0, 1);

        assertEquals(
                "New slot already booked.",
                result
        );
    }
}
