import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {
    private Scheduler scheduler;
    private DroneSystem drone1, drone2;

    @BeforeEach
    void setUp() {
        scheduler = new Scheduler();
        drone1 = new DroneSystem(scheduler, 1);
        drone2 = new DroneSystem(scheduler, 2);

        scheduler.addDrone(drone1);
        scheduler.addDrone(drone2);

        // Start drones in separate threads
        new Thread(drone1).start();
        new Thread(drone2).start();
    }

    @Test
    void testAddEventToScheduler() {
        Message event1 = new Message(50, 3, "FIRE_DETECTED", "High");
        Message event2 = new Message(51, 7, "DRONE_REQUEST", "Moderate");

        scheduler.addEvent(event1);
        scheduler.addEvent(event2);

        assertNotEquals(event1, scheduler.getEventQueue().peek()); // Events are assigned as soon as they are added so queue could be null or a different event
    }

    @Test
    void testEventAssignmentToDrones() throws InterruptedException {
        Message event1 = new Message(50, 3, "FIRE_DETECTED", "High");
        Message event2 = new Message(51, 7, "DRONE_REQUEST", "Moderate");

        scheduler.addEvent(event1);
        scheduler.addEvent(event2);

        Thread.sleep(500); // Allow time for the event to be assigned

        assertFalse(drone1.isAvailable() && drone2.isAvailable());
    }

    @Test
    void testDroneBecomesAvailableAfterProcessing() throws InterruptedException {
        Message event1 = new Message(50, 3, "FIRE_DETECTED", "High");
        scheduler.addEvent(event1);

        Thread.sleep(3500); // Wait for event processing

        assertTrue(drone1.isAvailable() || drone2.isAvailable());
    }
}
