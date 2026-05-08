package Service_Layer;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;

public class AppointmentModificationServiceTest {

    private AppointmentModificationService service;
    private AppointmentSlotsManager slotsManager;

    @BeforeEach
    public void setup() {

        service = AppointmentModificationService.getInstance();
        slotsManager = AppointmentSlotsManager.getInstance();

        Schedule schedule = new Schedule();

        // slot 0 (محجوز للمستخدم)
        TimeSlot bookedSlot = new TimeSlot(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1)
        );

        bookedSlot.book(
                "user@gmail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        // slot 1 (فارغ)
        TimeSlot freeSlot = new TimeSlot(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1)
        );

        // slot 2 (محجوز)
        TimeSlot bookedSlot2 = new TimeSlot(
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(3).plusHours(1)
        );

        bookedSlot2.book(
                "other@gmail.com",
                AppointmentType.GROUP,
                3
        );

        schedule.addSlot(bookedSlot);
        schedule.addSlot(freeSlot);
        schedule.addSlot(bookedSlot2);

        slotsManager.saveSchedule(schedule);
    }

    // تعديل ناجح
    @Test
    public void testModifyAppointmentSuccess() {

        String result = service.modifyAppointment(
                "user@gmail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("Modified successfully.", result);
    }

    // user ما عنده booking
    @Test
    public void testModifyNoBookingFound() {

        String result = service.modifyAppointment(
                "notfound@gmail.com",
                1,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("No booking found for this user.", result);
    }

    // index غلط
    @Test
    public void testModifyInvalidIndex() {

        String result = service.modifyAppointment(
                "user@gmail.com",
                99,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("Invalid index.", result);
    }

    // new slot محجوز
    @Test
    public void testModifyNewSlotAlreadyBooked() {

        String result = service.modifyAppointment(
                "user@gmail.com",
                2,
                AppointmentType.INDIVIDUAL
        );

        assertEquals("New slot already booked.", result);
    }

    // admin تعديل ناجح
    @Test
    public void testModifyByAdminSuccess() {

        String result = service.modifyAppointmentByAdmin(2, 1);

        assertEquals("Modified by admin.", result);
    }

    // admin invalid index
    @Test
    public void testModifyByAdminInvalidIndex() {

        String result = service.modifyAppointmentByAdmin(99, 1);

        assertEquals("Invalid index.", result);
    }

    // old slot مش محجوز
    @Test
    public void testModifyByAdminOldNotBooked() {

        String result = service.modifyAppointmentByAdmin(1, 2);

        assertEquals("Old slot not booked.", result);
    }

    // new slot محجوز
    @Test
    public void testModifyByAdminNewAlreadyBooked() {

        String result = service.modifyAppointmentByAdmin(0, 2);

        assertEquals("New slot already booked.", result);
    }

    // singleton not null
    @Test
    public void testSingletonNotNull() {

        assertNotNull(service);
    }

    // singleton same instance
    @Test
    public void testSingletonSameInstance() {

        AppointmentModificationService s1 =
                AppointmentModificationService.getInstance();

        AppointmentModificationService s2 =
                AppointmentModificationService.getInstance();

        assertSame(s1, s2);
    }
}