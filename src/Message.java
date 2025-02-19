public class Message {
    private int timestamp;
    private int zoneID;
    private String eventType;

    private String severity;


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
     * gets the type of event FIRE_DETECTED or a DRONE_REQUEST.
     *
     * @return The event type as a String.
     */
    public String getEventType() {
        return eventType;
    }

    public String getSeverity() {
        return severity;
    }


    @Override
    public String toString() {
        return "Event {" +
                "timestamp='" + timestamp + 's' + '\'' +
                ", zoneID=" + zoneID +
                ", eventType='" + eventType + '\'' +
                ", severity='" + severity + '\'' +
                '}';
    }
}
