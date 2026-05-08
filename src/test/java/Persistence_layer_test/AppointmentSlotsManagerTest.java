package Persistence_layer_test;

import org.junit.jupiter.api.*;
import Domain_Layer.AppointmentType;
import Domain_Layer.Schedule;
import Domain_Layer.TimeSlot;
import Persistence_Layer.AppointmentSlotsManager;
import Service_Layer.AppointmentService;
import presentation_Layer.Appointment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentSlotsManagerTest {

    private static final String SLOT_FILE = "schedule.txt";

    @BeforeEach
    void cleanUpBefore() throws Exception {
        Files.deleteIfExists(Paths.get(SLOT_FILE));
        Appointment.selectedLow = 0;
        Appointment.selectedHigh = 0;
    }

    @Test
    void testBuildScheduleCreatesSlots() {
        AppointmentSlotsManager manager = AppointmentSlotsManager.getInstance();
        Schedule schedule = manager.buildSchedule(9, 12);
        assertNotNull(schedule);
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    void testSaveAndLoadSchedule() {
        AppointmentSlotsManager manager = AppointmentSlotsManager.getInstance();
        Schedule schedule = manager.buildSchedule(9, 10);
        manager.saveSchedule(schedule);
        Schedule loaded = manager.loadSchedule();
        assertEquals(schedule.getSlots().size(), loaded.getSlots().size());
    }

    @Test
    void testLoadScheduleWhenFileDoesNotExist() {
        AppointmentSlotsManager manager = AppointmentSlotsManager.getInstance();
        Schedule schedule = manager.loadSchedule();
        assertNotNull(schedule);
    }

    @Test
    void testLoadScheduleWithSelectedLowHigh() {
        Appointment.selectedLow = 8;
        Appointment.selectedHigh = 9;
        AppointmentSlotsManager manager = AppointmentSlotsManager.getInstance();
      
        Schedule schedule = manager.loadSchedule();
        assertFalse(schedule.getSlots().isEmpty());
    }

    @Test
    void testLoadScheduleWithMalformedLine() throws Exception {
        Files.write(Paths.get(SLOT_FILE), "bad,line,with,too,few,parts".getBytes());
        AppointmentSlotsManager manager = AppointmentSlotsManager.getInstance();
        Schedule schedule = manager.loadSchedule();
        assertNotNull(schedule);
    }

    @Test
    void testViewAvailableSlotsOutput() {
        AppointmentSlotsManager manager = AppointmentSlotsManager.getInstance();
        Schedule schedule = new Schedule();
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
        LocalDateTime end = start.plusMinutes(AppointmentService.MAX_DURATION_MINUTES);
        TimeSlot slot = new TimeSlot(start, end);
        slot.book("user@example.com", AppointmentType.GROUP, 2);
        schedule.addSlot(slot);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        manager.viewAvailableSlots(schedule);
        String output = out.toString();
        assertTrue(output.contains("Booked"));
        assertTrue(output.contains("GROUP"));
    }

    @Test
    void testSendRemindersForTomorrow() {
        AppointmentSlotsManager manager = AppointmentSlotsManager.getInstance();
        Schedule schedule = new Schedule();
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        LocalDateTime end = start.plusMinutes(AppointmentService.MAX_DURATION_MINUTES);
        TimeSlot slot = new TimeSlot(start, end);
        slot.book("test@example.com", AppointmentType.GROUP, 1);
        schedule.addSlot(slot);
        manager.saveSchedule(schedule);
        manager.sendReminders();
        assertTrue(slot.isBooked());
    }
}
