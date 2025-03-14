/**
 * Represents a message or event related to a fire incident or a drone request.
 * This class stores information such as the timestamp, zone ID, event type, and severity level.
 * It is used to capture event data in response to fire-related incidents or drone-related events.
 */
public class Message {
    private int timestamp;
    private int zoneID;
    private String eventType;

    private String severity;
    private int fireLevel;


    /**
     * Constructs a new FireIncident object.
     *
     * @param timestamp the time the incident occurred we use this  (hh:mm:ss format).
     * @param zoneID The ID of the zone where the fire was detected.
     * @param eventType The type of event (FIRE_DETECTED or DRONE_REQUEST).
     * @param severity The severity level of the fire (Low, Moderate, High).
     */
    public Message(int timestamp,int zoneID, String eventType,String severity){
        this.timestamp = timestamp;
        this.zoneID = zoneID;
        this.eventType = eventType;
        this.severity=severity;

        switch (severity){
            case "High":
                fireLevel= 30;
                break;
            case "Medium":
                fireLevel = 20;
                break;
            case "Low":
                fireLevel = 10;
                break;
        }

    }


    /**
     * gets the timestamp of the FIreIncident.
     *
     * @return The timestamp as a String.
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * gets the ID of the zone where FireIncident occurred.
     *
     * @return The zone ID as an integer.
     */
    public int getZoneID() {
        return zoneID;
    }


    /**
     * gets the type of event FIRE_DETECTED or a DRONE_REQUEST or a DRONE_CRASH.
     *
     * @return The event type as a String.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * gets the severity of the event
     *
     * @return The event type as a String.
     */
    public String getSeverity() {
        return severity;
    }


    /**
     * Returns a string representation of the Event object.
     *
     * @return A formatted string containing the Event.
     */
    @Override
    public String toString() {
        return "Event {" +
                "timestamp='" + timestamp + 's' + '\'' +
                ", zoneID=" + zoneID +
                ", eventType='" + eventType + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }

    /**
     * reduces the fire level by a certain amount
     */
    public void reduceFire() {
    }


    /**
     * @return the level of the fire in this zone
     */
    public int getFireLevel() {
        return 0;
    }
}