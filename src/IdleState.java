/**
 * Represents the state where the Scheduler is idle, waiting for events to be added.
 * Transitions to ProcessingState when a new event is received.
 */
public class IdleState implements SchedulerState {
    /**
     * Adds an event to the scheduler and transitions to ProcessingState.
     *
     * @param scheduler The Scheduler instance.
     * @param event The event to be added.
     */
    @Override
    public void addEvent(Scheduler scheduler, Message event) {

        System.out.println("[Scheduler] Event added in IdleState: " + event);

        // Transition to ProcessingState
        scheduler.setState(new ProcessingState());
        
        //assignEventToDrone(scheduler);
    }

    /**
     * No events are available for assignment, so the scheduler remains in IdleState.
     *
     * @param scheduler The Scheduler instance.
     */
    @Override
    public void assignEventToDrone(Scheduler scheduler) {
        System.out.println("[Scheduler] No events to assign. Remaining in Idle state." + "\n");
    }
}
