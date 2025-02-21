public class WaitingState implements SchedulerState {
    @Override
    public void addEvent(Scheduler scheduler, Message event) {
        synchronized (scheduler) {
            scheduler.getEventQueue().add(event);
            System.out.println("[Scheduler] Added event in waiting state: " + event);
            assignEventToDrone(scheduler);
            scheduler.notifyAll(); // Wake up in case a drone becomes available
        }
    }

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

            scheduler.setState(new ProcessingState());
            scheduler.notifyAll(); // Notify that we're back in processing mode
            //scheduler.assignEventToDrone();
        }
    }
}
