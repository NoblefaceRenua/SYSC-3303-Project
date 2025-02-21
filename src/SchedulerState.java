public interface SchedulerState {
    void addEvent(Scheduler scheduler, Message event);
    void assignEventToDrone(Scheduler scheduler);
}
