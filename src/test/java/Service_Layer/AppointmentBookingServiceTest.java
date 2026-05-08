package Service_Layer;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;

public class AppointmentBookingServiceTest {

    private AppointmentBookingService service;
    private AppointmentSlotsManager slotsManager;

    @BeforeEach
    public void setup() {

        service = AppointmentBookingService.getInstance();
        slotsManager = AppointmentSlotsManager.getInstance();

        Schedule schedule = new Schedule();

        // موعد متاح
        TimeSlot futureSlot = new TimeSlot(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1)
        );

        // موعد محجوز
        TimeSlot bookedSlot = new TimeSlot(
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(1)
        );

        bookedSlot.book(
                "booked@gmail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        // موعد بالماضي
        TimeSlot pastSlot = new TimeSlot(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1).plusHours(1)
        );

        schedule.addSlot(futureSlot);
        schedule.addSlot(bookedSlot);
        schedule.addSlot(pastSlot);

        slotsManager.saveSchedule(schedule);
    }

    @Test
    public void testBookAppointmentSuccess() {

        String result = service.bookAppointment(
                0,
                "user@gmail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals(
                "Booked successfully with 1 participants.",
                result
        );
    }

    @Test
    public void testInvalidIndex() {

        String result = service.bookAppointment(
                100,
                "user@gmail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals("Invalid index.", result);
    }

    @Test
    public void testPastAppointment() {

        String result = service.bookAppointment(
                2,
                "user@gmail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals(
                "Cannot book past appointments.",
                result
        );
    }

    @Test
    public void testAlreadyBooked() {

        String result = service.bookAppointment(
                1,
                "another@gmail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals("Already booked.", result);
    }

    @Test
    public void testUserAlreadyHasBooking() {

        String result = service.bookAppointment(
                0,
                "booked@gmail.com",
                AppointmentType.INDIVIDUAL,
                1
        );

        assertEquals(
                "User already has a booking.",
                result
        );
    }

    @Test
    public void testSingletonNotNull() {

        assertNotNull(service);
    }

    @Test
    public void testSingletonSameInstance() {

        AppointmentBookingService s1 =
                AppointmentBookingService.getInstance();

        AppointmentBookingService s2 =
                AppointmentBookingService.getInstance();

        assertSame(s1, s2);
    }
}