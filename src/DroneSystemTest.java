import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class DroneSystemTest {
    private Scheduler scheduler;
    private DroneSystem droneSystem;
    private int id;
    private enum droneStatus {EMPTY, FULL, CRASH, ARRIVED};

    @BeforeEach
    void setUp() {
        this.scheduler = new Scheduler();
        this.id = 5;
        droneSystem = new DroneSystem(id, 5005);

        new Thread(droneSystem).start();
    }

    @Test
    void testRefillTank() {
        droneSystem.refillTank();
        assertTrue(droneSystem.getWaterLevel() == 100);
    }

    @Test
    void testPour() {
        droneSystem.pour();
        assertFalse(droneSystem.getArrivalSensor());
        assertFalse(droneSystem.getNozzle());
        assertTrue(droneSystem.getTravel_seconds_spent() == 0);
    }

    @Test
    void testMakeUnavailable() {

        assertTrue(droneSystem.makeUnavailable());
    }

}
