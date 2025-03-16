import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {
    private Scheduler scheduler;
    private DroneSystem drone1, drone2;

    @BeforeEach
    void setUp() {
        scheduler = new Scheduler();
    }

    @Test
    void testSchedulerInitialization() {
        // Test that the scheduler starts with an empty event queue
        assertNotNull(scheduler.getEventQueue(), "Event queue should be initialized");
        assertTrue(scheduler.getEventQueue().isEmpty(), "Event queue should be empty on initialization");
    }
    

    @Test
    void testSchedulerStateTransition() {
        // Test that the scheduler's internal state can be updated and checked

        // Assuming the scheduler has an internal state like "IDLE" or "PROCESSING"
        scheduler.setState(new ProcessingState());  // Simulate state change
        assertEquals(new ProcessingState(), scheduler.getState(), "Scheduler state should reflect 'PROCESSING' after update");

        scheduler.setState(new IdleState());  // Simulate another state change
        assertEquals(new IdleState(), scheduler.getState(), "Scheduler state should reflect 'IDLE' after update");
    }
    
}
