public class BusStop {
    private final String stopId;
    private final String stopName;

    public BusStop(String stopId, String stopName) {
        this.stopId = stopId;
        this.stopName = stopName;
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }

    @Override
    public String toString() {
        return stopId + " - " + stopName;
    }
}