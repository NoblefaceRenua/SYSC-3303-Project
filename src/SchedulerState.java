/**
 * Represents the state of the Scheduler in the drone firefighting system.
 * Implementations define how the Scheduler handles events and assigns drones.
 */
public interface SchedulerState {
    /**
     * Adds an event to the scheduler's event queue.
     *
     * @param scheduler The Scheduler instance managing events and drones.
     * @param event     The event to be added.
     */
    void addEvent(Scheduler scheduler, Message event);

    /**
     * Assigns an event from the queue to an available drone.
     *
     * @param scheduler The Scheduler instance managing events and drones.
     */
    void assignEventToDrone(Scheduler scheduler);
}
