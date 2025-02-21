/**
 * The Zone class represents a specific area where a fire incident can occur.
 * Each zone is defined by a unique zone ID and its coordinates on a grid.
 */
class Zone {

    private int zoneID, startX, startY, endX, endY;

    /**
     * Constructs a Zone object with a unique ID and coordinate boundaries.
     *
     * @param zoneID The unique identifier for the zone.
     * @param startX The X-coordinate of the starting boundary.
     * @param startY The Y-coordinate of the starting boundary.
     * @param endX The X-coordinate of the ending boundary.
     * @param endY The Y-coordinate of the ending boundary.
     */
    public Zone(int zoneID, int startX, int startY, int endX, int endY) {
        this.zoneID = zoneID;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * Returns a string representation of the Zone object.
     *
     * @return A formatted string containing the zone ID and its coordinate boundaries.
     */
    @Override
    public String toString() {
        return "Zone Loaded: " +
                "ID: " + zoneID +
                ", " + "(" + startX + ";" + startY + ")" +
                ", " + "(" + endX + ";" + endY + ")";
    }
}