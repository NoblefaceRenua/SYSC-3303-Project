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
        synchronized (scheduler) {
            scheduler.getEventQueue().add(event);
            System.out.println("[Scheduler] Added event in waiting state: " + event);
            assignEventToDrone(scheduler);
            scheduler.notifyAll(); // Wake up in case a drone becomes available
        }
    }

    /**
     * Waits for a drone to become available before transitioning to ProcessingState.
     *
     * @param scheduler The Scheduler instance.
     */
    @Override
    public void assignEventToDrone(Scheduler scheduler) {
        synchronized (scheduler) {
            while (scheduler.allDronesFull()) {
                try {
                    System.out.println("[Scheduler] No available drones. Remaining in Waiting state.");
                    scheduler.wait(); // Wait for a drone to become available
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Transition back to ProcessingState once a drone is available.
            scheduler.setState(new ProcessingState());
            scheduler.notifyAll(); // Notify that we're back in processing mode
        }
    }
}
