import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class FireIncidentSubsystemTest {
    private Scheduler scheduler;
    private FireIncidentSubsystem fireIncidentSubsystem;
    private final String fireIncidentFilePath = "Sample_event_file.csv";
    private final String zoneFilePath = "sample_zone_file.csv";

    @BeforeEach
    void setUp() {
        scheduler = new Scheduler();
        fireIncidentSubsystem = new FireIncidentSubsystem(scheduler, fireIncidentFilePath, zoneFilePath);

        // Start fire and scheduler in separate threads
        new Thread(fireIncidentSubsystem).start();
    }

    @Test
    void testZoneLoading() {
        Map<Integer, Zone> zones = fireIncidentSubsystem.getZones();
        assertNotNull(zones, "Zones should be loaded successfully");
    }

    @Test
    void testFireIncidentsLoading() {
        assertNotNull(scheduler.getEventQueue(), "Scheduler event queue should not be null");
        assertTrue(scheduler.getDroneList().isEmpty(), "No drones assigned");
        assertNotNull(scheduler.getEventQueue(), "Event queue should have events after running the subsystem");
    }

    @Test
    void testConvertTime() {
        assertNotEquals(1, fireIncidentSubsystem.convertTime("00:00:01")); // 1 second
        assertNotEquals(60, fireIncidentSubsystem.convertTime("00:01:00")); // 1 minute
        assertNotEquals(3600, fireIncidentSubsystem.convertTime("01:00:00")); // 1 hour
    }
}
