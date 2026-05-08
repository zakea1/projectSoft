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

        // تنظيف كامل + إعادة تهيئة الـ Schedule قبل كل تيست
        Schedule empty = new Schedule();
        AppointmentSlotsManager.getInstance().saveSchedule(empty);

        Schedule schedule = new Schedule();

        // Slots واضحة وثابتة
        TimeSlot pastSlot     = new TimeSlot(LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1));
        TimeSlot futureSlot1  = new TimeSlot(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));  // index 1
        TimeSlot futureSlot2  = new TimeSlot(LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4));  // index 2
        TimeSlot futureSlot3  = new TimeSlot(LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6));  // index 3

        schedule.addSlot(pastSlot);
        schedule.addSlot(futureSlot1);
        schedule.addSlot(futureSlot2);
        schedule.addSlot(futureSlot3);

        AppointmentSlotsManager.getInstance().saveSchedule(schedule);
    }

    // ===================== BOOK =====================

    @Test
    void testBookInvalidIndex() {
        String result = service.bookAppointment(-1, "user@test.com", AppointmentType.URGENT, 2);
        assertEquals("Invalid index.", result);
    }

    @Test
    void testBookPastAppointment() {
        String result = service.bookAppointment(0, "user@test.com", AppointmentType.URGENT, 2);
        assertEquals("Cannot book past appointments.", result);
    }

    @Test
    void testBookAlreadyBooked() {
        service.bookAppointment(1, "user@test.com", AppointmentType.URGENT, 2);
        String result = service.bookAppointment(1, "other@test.com", AppointmentType.URGENT, 2);
        assertEquals("Already booked.", result);
    }

    @Test
    void testUserAlreadyHasBooking() {
        service.bookAppointment(1, "user@test.com", AppointmentType.URGENT, 2);
        String result = service.bookAppointment(2, "user@test.com", AppointmentType.URGENT, 2);
        assertEquals("User already has a booking.", result);
    }

    @Test
    void testBookSuccess() {
        String result = service.bookAppointment(1, "success@test.com", AppointmentType.URGENT, 2);
        assertTrue(result.contains("Booked successfully"));
    }

    // ===================== CANCEL =====================

    @Test
    void testCancelAppointment() {
        service.bookAppointment(1, "cancel@test.com", AppointmentType.URGENT, 2);
        String result = service.cancelAppointment("cancel@test.com");
        assertEquals("Cancelled.", result);
    }

    @Test
    void testCancelAppointmentNoBooking() {
        String result = service.cancelAppointment("nobooking@test.com");
        assertEquals("No booking found.", result);
    }

    @Test
    void testCancelAppointmentByAdminInvalidIndex() {
        String result = service.cancelAppointmentByAdmin(-1);
        assertEquals("Invalid index.", result);
    }

    @Test
    void testCancelAppointmentByAdminAlreadyFree() {
        String result = service.cancelAppointmentByAdmin(3);
        assertEquals("Already free.", result);
    }

    @Test
    void testCancelAppointmentByAdminSuccess() {
        service.bookAppointment(2, "admincancel@test.com", AppointmentType.URGENT, 2);
        String result = service.cancelAppointmentByAdmin(2);
        assertEquals("Cancelled by admin.", result);
    }

    // ===================== MODIFY =====================

    @Test
    void testModifyAppointmentInvalidIndex() {
        String result = service.modifyAppointment("user@test.com", -1, AppointmentType.URGENT);
        assertEquals("Invalid index.", result);
    }

    @Test
    void testModifyAppointmentNoBooking() {
        String result = service.modifyAppointment("unknown@test.com", 2, AppointmentType.URGENT);
        assertEquals("No booking found.", result);
    }

   
    @Test
    void testModifyAppointmentSuccess() {
        service.bookAppointment(1, "modifyuser@test.com", AppointmentType.URGENT, 2);
        String result = service.modifyAppointment("modifyuser@test.com", 3, AppointmentType.URGENT);
        assertEquals("Modified successfully.", result);
    }

    @Test
    void testModifyAppointmentByAdminInvalidIndex() {
        String result = service.modifyAppointmentByAdmin(-1, 2);
        assertEquals("Invalid index.", result);
    }

    @Test
    void testModifyAppointmentByAdminOldSlotNotBooked() {
        String result = service.modifyAppointmentByAdmin(3, 1);
        assertEquals("Old slot not booked.", result);
    }

    @Test
    void testModifyAppointmentByAdminNewSlotAlreadyBooked() {
        service.bookAppointment(1, "u1@test.com", AppointmentType.URGENT, 2);
        service.bookAppointment(2, "u2@test.com", AppointmentType.URGENT, 2);
        String result = service.modifyAppointmentByAdmin(1, 2);
        assertEquals("New slot already booked.", result);
    }

    @Test
    void testModifyAppointmentByAdminSuccess() {
        service.bookAppointment(1, "adminmodify@test.com", AppointmentType.URGENT, 2);
        String result = service.modifyAppointmentByAdmin(1, 3);
        assertEquals("Modified by admin.", result);
    }
}