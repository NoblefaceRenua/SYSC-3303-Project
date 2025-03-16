/**
 * Represents the state where the Scheduler is waiting for drones to become available.
 * Once a drone is available, the Scheduler transitions back to ProcessingState.
 */
public class WaitingState implements SchedulerState {
    /**
     * Adds an event to the scheduler while it is waiting for available drones.
     *
     * @param scheduler The Scheduler instance.
     * @param event The event to be added.
     */
    @Override
    public void addEvent(Scheduler scheduler, Message event) {
        new Thread(() -> {
            synchronized (scheduler) {
                //scheduler.getEventQueue().add(event);
                System.out.println("[Scheduler] Added event in waiting state: " + event + "\n");
                assignEventToDrone(scheduler);
                //scheduler.notifyAll(); // Wake up in case a drone becomes available
            }
        }).start();
    }

    /**
     * Waits for a drone to become available before transitioning to ProcessingState.
     *
     * @param scheduler The Scheduler instance.
     */
    @Override
    public void assignEventToDrone(Scheduler scheduler) {
        new Thread(() -> {
            synchronized (scheduler) {
                if (scheduler.allDronesFull()) {
                    System.out.println("[Scheduler] No available drones. Remaining in Waiting state." + "\n");

                    try {
                        Thread.sleep(1000); // Brief pause before re-checking availability
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // Transition back to ProcessingState once a drone is available.
                System.out.println("[Scheduler] Drone available. Switching to ProcessingState." + "\n");
                scheduler.setState(new ProcessingState());
            }
        }).start();
    }
}
