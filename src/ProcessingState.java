import java.util.Map;

/**
 * Represents the state where the Scheduler is actively processing and assigning events to drones.
 * If all drones are busy, the Scheduler transitions to WaitingState.
 */
public class ProcessingState implements SchedulerState {
    /**
     * Adds an event to the scheduler's event queue and notifies waiting threads.
     *
     * @param scheduler The Scheduler instance.
     * @param event The event to be added.
     */
    @Override
    public void addEvent(Scheduler scheduler, Message event) {

        new Thread(() -> {
            synchronized (scheduler) {
                scheduler.getEventQueue().add(event);
                System.out.println("[Scheduler] Event added in ProcessingState: " + event + "\n");
                assignEventToDrone(scheduler);
            }
        }).start();
        //scheduler.notifyAll();  // Wake up any waiting threads
    }

    /**
     * Assigns events from the queue to available drones. If all drones are busy, transitions to WaitingState.
     *
     * @param scheduler The Scheduler instance.
     */
    @Override
    public void assignEventToDrone(Scheduler scheduler) {

        new Thread(() -> {
            synchronized (scheduler) {
                if (!scheduler.getEventQueue().isEmpty() && !scheduler.allDronesFull()) {
                    for (Map.Entry<Integer, Boolean> entry : scheduler.getDroneAvailability().entrySet()) {
                        if (entry.getValue()) { // Drone available
                            int dronePort = entry.getKey();

                            Message event = scheduler.eventQueue.poll();  // Get the next event
                            if (event != null) {
                                scheduler.rpcSendEventDrone(dronePort, event);

                                // Mark drone as busy
                                scheduler.getDroneAvailability().put(dronePort, false);
                            }
                        }
                    }

                    // State Transition Logic
                    if (scheduler.getEventQueue().isEmpty()) {
                        scheduler.setState(new IdleState());
                    } else {
                        scheduler.setState(new WaitingState());
                    }
                }
            }
        }).start();
    }
}
