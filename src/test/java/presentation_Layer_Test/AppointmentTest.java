package presentation_Layer_Test;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import presentation_Layer.Appointment;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentTest{

    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }

    @Test
    void testSavedConfigLoadedWithMockito() {
        // نعمل Mock للـ AdminTimeConfig
        Persistence_Layer.AdminTimeConfig mockConfig = Mockito.mock(Persistence_Layer.AdminTimeConfig.class);
        Mockito.when(mockConfig.loadCurrentRangeAndDuration()).thenReturn(new int[]{8, 14, 30});
        Appointment.adminTimeConfig = mockConfig;

        // نعمل Mock للـ SlotsManager
        Persistence_Layer.AppointmentSlotsManager mockSlots = Mockito.mock(Persistence_Layer.AppointmentSlotsManager.class);
        Mockito.when(mockSlots.loadSchedule()).thenReturn(new Domain_Layer.Schedule());
        Appointment.slots = mockSlots;

        String input = "3\n"; // خروج
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Appointment.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Using the previously saved time range"));
        assertTrue(output.contains("Thank you! Goodbye."));
    }
}
