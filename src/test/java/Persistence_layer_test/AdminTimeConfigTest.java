package Persistence_layer_test;


import org.junit.jupiter.api.*;

import Persistence_Layer.AdminTimeConfig;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class AdminTimeConfigTest {

    private static final String CONFIG_FILE = "admin_time_range.txt";

    @BeforeEach
    void cleanUpBefore() throws Exception {
        Files.deleteIfExists(Paths.get(CONFIG_FILE));
    }

    @Test
    void testSaveAndLoadCurrentWeek() {
        AdminTimeConfig config = AdminTimeConfig.getInstance();
        config.saveRangeAndDuration(9, 17, 30);
        int[] result = config.loadCurrentRangeAndDuration();
        assertNotNull(result);
        assertEquals(9, result[0]);
        assertEquals(17, result[1]);
        assertEquals(30, result[2]);
    }

    @Test
    void testLoadInvalidFile() throws Exception {
        Files.write(Paths.get(CONFIG_FILE), "bad,data".getBytes());
        AdminTimeConfig config = AdminTimeConfig.getInstance();
        int[] result = config.loadCurrentRangeAndDuration();
        assertNull(result);
    }

    @Test
    void testLoadWhenFileDoesNotExist() {
        AdminTimeConfig config = AdminTimeConfig.getInstance();
        int[] result = config.loadCurrentRangeAndDuration();
        assertNull(result);
    }

    @Test
    void testLoadDifferentWeek() throws Exception {
        int fakeWeek = 1;
        String content = fakeWeek + ",8,16,45";
        Files.write(Paths.get(CONFIG_FILE), content.getBytes());
        AdminTimeConfig config = AdminTimeConfig.getInstance();
        int[] result = config.loadCurrentRangeAndDuration();
        assertNull(result);
    }
}


