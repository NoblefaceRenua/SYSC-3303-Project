class Zone {

    private int zoneID, startX, startY, endX, endY;

    public Zone(int zoneID, int startX, int startY, int endX, int endY) {
        this.zoneID = zoneID;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    public String toString() {
        return "Zone Loaded: " +
                "ID: " + zoneID +
                ", " + "(" + startX + ";" + startY + ")" +
                ", " + "(" + endX + ";" + endY + ")";
    }
}