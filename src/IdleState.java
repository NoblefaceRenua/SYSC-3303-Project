public class IdleState implements SchedulerState {
    @Override
    public void addEvent(Scheduler scheduler, Message event) {
        scheduler.eventQueue.add(event);
        System.out.println("[Scheduler] Event added: " + event);

        // Transition to ProcessingState
        scheduler.setState(new ProcessingState());
        assignEventToDrone(scheduler);
    }

    @Override
    public void assignEventToDrone(Scheduler scheduler) {
        System.out.println("[Scheduler] No events to assign. Remaining in Idle state.");
    }
}
