package Service_Layer;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Domain_Layer.AppointmentType;
import Persistence_Layer.AppointmentSlotsManager;

public class AppointmentCancellationServiceTest {

    private AppointmentCancellationService service;
    private AppointmentSlotsManager slotsManager;

    @BeforeEach
    public void setup() {

        service = AppointmentCancellationService.getInstance();
        slotsManager = AppointmentSlotsManager.getInstance();

        Schedule schedule = new Schedule();

        // Slot محجوز لمستخدم
        TimeSlot bookedSlot = new TimeSlot(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1)
        );

        bookedSlot.book(
                "user@gmail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        // Slot فاضي
        TimeSlot freeSlot = new TimeSlot(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1)
        );

        schedule.addSlot(bookedSlot);
        schedule.addSlot(freeSlot);

        slotsManager.saveSchedule(schedule);
    }

    // cancel by user success
    @Test
    public void testCancelByUserSuccess() {

        String result = service.cancelAppointment("user@gmail.com");

        assertEquals("Cancelled successfully.", result);
    }

    // cancel by user not found
    @Test
    public void testCancelByUserNotFound() {

        String result = service.cancelAppointment("notfound@gmail.com");

        assertEquals("No booking found for this user.", result);
    }

    // cancel by admin success
    @Test
    public void testCancelByAdminSuccess() {

        String result = service.cancelAppointmentByAdmin(0);

        assertEquals("Cancelled by admin.", result);
    }

    // cancel by admin invalid index
    @Test
    public void testCancelByAdminInvalidIndex() {

        String result = service.cancelAppointmentByAdmin(99);

        assertEquals("Invalid index.", result);
    }

    // cancel by admin already free slot
    @Test
    public void testCancelByAdminFreeSlot() {

        String result = service.cancelAppointmentByAdmin(1);

        assertEquals("Slot is already free.", result);
    }

    // singleton not null
    @Test
    public void testSingletonNotNull() {

        assertNotNull(service);
    }

    // singleton same instance
    @Test
    public void testSingletonSameInstance() {

        AppointmentCancellationService s1 =
                AppointmentCancellationService.getInstance();

        AppointmentCancellationService s2 =
                AppointmentCancellationService.getInstance();

        assertSame(s1, s2);
    }
}