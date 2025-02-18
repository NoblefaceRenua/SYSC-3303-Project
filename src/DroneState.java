public interface DroneState {
    void handleStateChanged(DroneSystem drone);

    void displayState();

    void handleDroneStuck(DroneSystem drone);

    void handleEmpty(DroneSystem drone);
}
