import java.util.Map;

public class ValidationService {

    public static boolean isValidStopId(String stopId) {
        return stopId != null && !stopId.trim().isEmpty();
    }

    public static boolean isValidStopName(String stopName) {
        return stopName != null && !stopName.trim().isEmpty();
    }

    public static boolean stopExists(Map<String, BusStop> stops, String stopId) {
        return stops.containsKey(normalize(stopId));
    }

    public static boolean validateDuplicateStop(Map<String, BusStop> stops, String stopId) {
        if (!isValidStopId(stopId)) {
            return false;
        }
        return !stopExists(stops, stopId);
    }

    public static boolean validateRouteInput(Map<String, BusStop> stops, Graph graph, String from, String to, int distance) {
        if (!isValidStopId(from) || !isValidStopId(to)) {
            return false;
        }

        String origin = normalize(from);
        String destination = normalize(to);

        if (!stopExists(stops, origin) || !stopExists(stops, destination)) {
            return false;
        }
        if (origin.equals(destination)) {
            return false;
        }
        if (distance <= 0) {
            return false;
        }
        return !graph.routeExists(origin, destination);
    }

    public static boolean validateGraphHasStops(Graph graph) {
        return !graph.getStops().isEmpty();
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}