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
        synchronized (scheduler) {
            scheduler.getEventQueue().add(event);
            System.out.println("[Scheduler] Event added: " + event);
            assignEventToDrone(scheduler);
            scheduler.notifyAll();  // Wake up any waiting threads
        }
    }

    /**
     * Assigns events from the queue to available drones. If all drones are busy, transitions to WaitingState.
     *
     * @param scheduler The Scheduler instance.
     */
    @Override
    public void assignEventToDrone(Scheduler scheduler) {

        synchronized (scheduler) {
            // Wait until there are events to process and at least one drone is available.
            while (scheduler.getEventQueue().isEmpty() || scheduler.allDronesFull()) {
                try {
                    System.out.println("[Scheduler] No Event or Drone Available");
                    scheduler.wait();  // Wait until an event is added or a drone becomes available
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }


            // Assign events while there are events or drones available.
            while (!scheduler.eventQueue.isEmpty() || !scheduler.allDronesFull()) {
                for (DroneSystem drone : scheduler.drones) {
                    if (drone.isAvailable()) {
                        Message event = scheduler.eventQueue.poll();  // Get the next event
                        if (event != null) {
                            drone.assignEvent(event);
                            System.out.println("[Scheduler] Assigned event to Drone " + drone.getId());
                            scheduler.notifyAll();  // Wake up waiting threads
                        }
                    }
                }
                // If events are still unprocessed due to full drones, transition to WaitingState.
                if (scheduler.eventQueue.isEmpty()) {
                    scheduler.setState(new IdleState());
                } else {
                    scheduler.setState(new WaitingState());
                }
            }


        }
    }
}
