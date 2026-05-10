package Service_Layer;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import Domain_Layer.*;
import Persistence_Layer.AppointmentSlotsManager;

public class AppointmentSlotsTest {

    private AppointmentSlots service;

    @BeforeEach
    void setup() {

        service = AppointmentSlots.getInstance();

        // تنظيف كامل
        Schedule empty = new Schedule();
        AppointmentSlotsManager.getInstance().saveSchedule(empty);

        Schedule schedule = new Schedule();

        // Slots ثابتة
        TimeSlot pastSlot =
                new TimeSlot(
                        LocalDateTime.now().minusHours(2),
                        LocalDateTime.now().minusHours(1));

        TimeSlot futureSlot1 =
                new TimeSlot(
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusHours(2));

        TimeSlot futureSlot2 =
                new TimeSlot(
                        LocalDateTime.now().plusHours(3),
                        LocalDateTime.now().plusHours(4));

        TimeSlot futureSlot3 =
                new TimeSlot(
                        LocalDateTime.now().plusHours(5),
                        LocalDateTime.now().plusHours(6));

        schedule.addSlot(pastSlot);     // index 0
        schedule.addSlot(futureSlot1);  // index 1
        schedule.addSlot(futureSlot2);  // index 2
        schedule.addSlot(futureSlot3);  // index 3

        AppointmentSlotsManager
                .getInstance()
                .saveSchedule(schedule);
    }

    // ===================== BOOK =====================

    @Test
    void testBookInvalidIndex() {

        String result =
                service.bookAppointment(
                        -1,
                        "user@test.com",
                        AppointmentType.URGENT,
                        2);

        assertEquals("Invalid index.", result);
    }

    @Test
    void testBookPastAppointment() {

        String result =
                service.bookAppointment(
                        0,
                        "user@test.com",
                        AppointmentType.URGENT,
                        2);

        assertEquals("Cannot book past appointments.", result);
    }

    @Test
    void testBookSuccessfully() {

        String result =
                service.bookAppointment(
                        1,
                        "user@test.com",
                        AppointmentType.URGENT,
                        2);

        assertEquals(
                "Booked successfully with 2 participants.",
                result);
    }

    @Test
    void testBookAlreadyBooked() {

        service.bookAppointment(
                1,
                "user@test.com",
                AppointmentType.URGENT,
                2);

        String result =
                service.bookAppointment(
                        1,
                        "another@test.com",
                        AppointmentType.URGENT,
                        2);

        assertEquals("Already booked.", result);
    }

    @Test
    void testUserAlreadyHasBooking() {

        service.bookAppointment(
                1,
                "user@test.com",
                AppointmentType.URGENT,
                2);

        String result =
                service.bookAppointment(
                        2,
                        "user@test.com",
                        AppointmentType.URGENT,
                        2);

        assertEquals("User already has a booking.", result);
    }

    // ===================== CANCEL =====================

    @Test
    void testCancelAppointmentSuccessfully() {

        service.bookAppointment(
                1,
                "user@test.com",
                AppointmentType.URGENT,
                2);

        String result =
                service.cancelAppointment("user@test.com");

        assertEquals("Cancelled.", result);
    }

    @Test
    void testCancelAppointmentNoBooking() {

        String result =
                service.cancelAppointment("nobooking@test.com");

        assertEquals("No booking found.", result);
    }

    @Test
    void testCancelAppointmentByAdminInvalidIndex() {

        String result =
                service.cancelAppointmentByAdmin(-1);

        assertEquals("Invalid index.", result);
    }

    @Test
    void testCancelAppointmentByAdminAlreadyFree() {

        String result =
                service.cancelAppointmentByAdmin(3);

        assertEquals("Already free.", result);
    }

    @Test
    void testCancelAppointmentByAdminSuccessfully() {

        service.bookAppointment(
                1,
                "user@test.com",
                AppointmentType.URGENT,
                2);

        String result =
                service.cancelAppointmentByAdmin(1);

        assertEquals("Cancelled by admin.", result);
    }

    // ===================== MODIFY =====================

    @Test
    void testModifyAppointmentInvalidIndex() {

        String result =
                service.modifyAppointment(
                        "user@test.com",
                        -1,
                        AppointmentType.URGENT);

        assertEquals("Invalid index.", result);
    }

    @Test
    void testModifyAppointmentNoBooking() {

        String result =
                service.modifyAppointment(
                        "unknown@test.com",
                        2,
                        AppointmentType.URGENT);

        assertEquals("No booking found.", result);
    }

    @Test
    void testModifyAppointmentSuccessfully() {

        service.bookAppointment(
                1,
                "user@test.com",
                AppointmentType.URGENT,
                2);

        String result =
                service.modifyAppointment(
                        "user@test.com",
                        2,
                        AppointmentType.REGULAR);

        assertEquals("Modified successfully.", result);
    }

    @Test
    void testModifyAppointmentNewSlotBooked() {

        service.bookAppointment(
                1,
                "user1@test.com",
                AppointmentType.URGENT,
                2);

        service.bookAppointment(
                2,
                "user2@test.com",
                AppointmentType.URGENT,
                2);

        String result =
                service.modifyAppointment(
                        "user1@test.com",
                        2,
                        AppointmentType.URGENT);

        assertEquals("New slot already booked.", result);
    }

    @Test
    void testModifyAppointmentByAdminInvalidIndex() {

        String result =
                service.modifyAppointmentByAdmin(-1, 2);

        assertEquals("Invalid index.", result);
    }

    @Test
    void testModifyAppointmentByAdminOldSlotNotBooked() {

        String result =
                service.modifyAppointmentByAdmin(3, 1);

        assertEquals("Old slot not booked.", result);
    }

    @Test
    void testModifyAppointmentByAdminSuccessfully() {

        service.bookAppointment(
                1,
                "user@test.com",
                AppointmentType.URGENT,
                2);

        String result =
                service.modifyAppointmentByAdmin(1, 2);

        assertEquals("Modified by admin.", result);
    }
}
