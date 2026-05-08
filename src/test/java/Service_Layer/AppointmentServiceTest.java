package Service_Layer;



import Domain_Layer.AppointmentType;
import Domain_Layer.TimeSlot;
import Service_Layer.AppointmentService.InvalidDurationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentServiceTest {

    private AppointmentService service;

    @BeforeEach
    void setUp() {
        // نحصل على الـ instance من Singleton
        service = AppointmentService.getInstance();
        // نرجع القيمة الافتراضية قبل كل تست
        AppointmentService.MAX_DURATION_MINUTES = 30;
    }

    // ============================
    // اختبارات Singleton Pattern
    // ============================

    @Test
    @DisplayName("getInstance should always return the same instance")
    void testSingletonInstance() {
        AppointmentService instance1 = AppointmentService.getInstance();
        AppointmentService instance2 = AppointmentService.getInstance();
        assertSame(instance1, instance2, "Singleton should return the same instance");
    }

    // ============================
    // اختبارات createAppointment
    // ============================

    @Test
    @DisplayName("Should book appointment successfully with valid data")
    void testCreateAppointment_Success() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 10, 9, 30);
        TimeSlot slot = new TimeSlot(start, end);

        String result = service.createAppointment(slot, AppointmentType.INDIVIDUAL, 1);

        assertTrue(result.contains("Appointment booked successfully"),
                "Should return success message");
    }

    @Test
    @DisplayName("Should throw InvalidDurationException when duration exceeds max")
    void testCreateAppointment_ExceedMaxDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 10, 12, 0); // 180 minutes
        TimeSlot slot = new TimeSlot(start, end);

        assertThrows(InvalidDurationException.class, () -> {
            service.createAppointment(slot, AppointmentType.INDIVIDUAL, 1);
        }, "Should throw InvalidDurationException for excessive duration");
    }

    @Test
    @DisplayName("Should return error for invalid number of participants")
    void testCreateAppointment_InvalidParticipants() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 10, 9, 30);
        TimeSlot slot = new TimeSlot(start, end);

        String result = service.createAppointment(slot, AppointmentType.INDIVIDUAL, 0);

        assertTrue(result.contains("Invalid number of participants"),
                "Should return invalid participants message");
    }

    @Test
    @DisplayName("Should book GROUP appointment successfully")
    void testCreateAppointment_GroupType() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 10, 9, 30);
        TimeSlot slot = new TimeSlot(start, end);

        String result = service.createAppointment(slot, AppointmentType.GROUP, 5);

        assertTrue(result.contains("Appointment booked successfully"),
                "Should book group appointment successfully");
        assertTrue(result.contains("GROUP"), "Should mention GROUP type");
    }

    @Test
    @DisplayName("Should include duration in success message")
    void testCreateAppointment_MessageContainsDuration() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 10, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 10, 9, 15);
        TimeSlot slot = new TimeSlot(start, end);

        String result = service.createAppointment(slot, AppointmentType.INDIVIDUAL, 1);

        assertTrue(result.contains("15 minutes"),
                "Success message should contain duration");
    }

    // ============================
    // اختبارات chooseAppointmentDuration
    // ============================

    @Test
    @DisplayName("Should set duration to 15 minutes when option 1 is selected")
    void testChooseDuration_15Minutes() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n".getBytes()));
        service.chooseAppointmentDuration(scanner);
        assertEquals(15, AppointmentService.MAX_DURATION_MINUTES);
    }

    @Test
    @DisplayName("Should set duration to 30 minutes when option 2 is selected")
    void testChooseDuration_30Minutes() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("2\n".getBytes()));
        service.chooseAppointmentDuration(scanner);
        assertEquals(30, AppointmentService.MAX_DURATION_MINUTES);
    }

    @Test
    @DisplayName("Should set duration to 45 minutes when option 3 is selected")
    void testChooseDuration_45Minutes() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("3\n".getBytes()));
        service.chooseAppointmentDuration(scanner);
        assertEquals(45, AppointmentService.MAX_DURATION_MINUTES);
    }

    @Test
    @DisplayName("Should set duration to 60 minutes when option 4 is selected")
    void testChooseDuration_60Minutes() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("4\n".getBytes()));
        service.chooseAppointmentDuration(scanner);
        assertEquals(60, AppointmentService.MAX_DURATION_MINUTES);
    }

    @Test
    @DisplayName("Should default to 30 minutes for invalid option number")
    void testChooseDuration_InvalidOption() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("9\n".getBytes()));
        service.chooseAppointmentDuration(scanner);
        assertEquals(30, AppointmentService.MAX_DURATION_MINUTES);
    }

    @Test
    @DisplayName("Should default to 30 minutes for non-numeric input")
    void testChooseDuration_NonNumericInput() {
        Scanner scanner = new Scanner(new ByteArrayInputStream("abc\n".getBytes()));
        service.chooseAppointmentDuration(scanner);
        assertEquals(30, AppointmentService.MAX_DURATION_MINUTES);
    }

    // ============================
    // اختبار InvalidDurationException
    // ============================

    @Test
    @DisplayName("InvalidDurationException should carry the correct message")
    void testInvalidDurationException_Message() {
        String msg = "Test error message";
        InvalidDurationException ex = new InvalidDurationException(msg);
        assertEquals(msg, ex.getMessage());
    }
}
