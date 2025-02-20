public interface DroneState {
    void handleStateChanged(DroneSystem drone);

    void displayState();
}