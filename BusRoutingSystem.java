import java.util.*;

public class BusRoutingSystem {
    private final Graph graph;
    private final Map<String, BusStop> stops;

    public BusRoutingSystem() {
        this.graph = new Graph();
        this.stops = new HashMap<>();
    }

    public void addBusStop(String stopId, String stopName) {
        String normalizedId = normalize(stopId);
        String normalizedName = normalize(stopName);

        if (!ValidationService.isValidStopId(normalizedId) || !ValidationService.isValidStopName(normalizedName)) {
            System.out.println("❌ Stop ID and name are required. Please provide valid values.");
            return;
        }

        if (!ValidationService.validateDuplicateStop(stops, normalizedId)) {
            System.out.println("❌ Duplicate stop ID not allowed.");
            return;
        }

        BusStop busStop = new BusStop(normalizedId, normalizedName);
        stops.put(normalizedId, busStop);
        graph.addStop(normalizedId);
        System.out.println("✔ Stop successfully added: " + busStop);
    }

    public void addRoute(String fromStopId, String toStopId, int distance) {
        String origin = normalize(fromStopId);
        String destination = normalize(toStopId);

        if (!ValidationService.isValidStopId(origin) || !ValidationService.isValidStopId(destination)) {
            System.out.println("❌ Invalid stop ID. Please enter valid source and destination IDs.");
            return;
        }

        if (!ValidationService.validateRouteInput(stops, graph, origin, destination, distance)) {
            if (!ValidationService.stopExists(stops, origin)) {
                System.out.println("❌ Stop does not exist. Please add it first: " + origin);
                return;
            }
            if (!ValidationService.stopExists(stops, destination)) {
                System.out.println("❌ Stop does not exist. Please add it first: " + destination);
                return;
            }
            if (origin.equals(destination)) {
                System.out.println("❌ Origin and destination must be different.");
                return;
            }
            if (distance <= 0) {
                System.out.println("❌ Distance must be a positive integer.");
                return;
            }
            if (graph.routeExists(origin, destination)) {
                System.out.println("❌ Route already exists between " + origin + " and " + destination + ".");
                return;
            }
            System.out.println("❌ Could not add route. Check your input and try again.");
            return;
        }

        graph.addRoute(origin, destination, distance);
        System.out.println("✔ Route successfully added: " + origin + " ↔ " + destination + " (" + distance + " km)");
    }

    public void showAllStops() {
        if (stops.isEmpty()) {
            System.out.println("No bus stops available.");
            return;
        }

        System.out.println("All Bus Stops:");
        stops.values().stream()
                .sorted(Comparator.comparing(BusStop::getStopId))
                .forEach(stop -> System.out.println("  " + stop));
    }

    public void showAllRoutes() {
        if (stops.isEmpty()) {
            System.out.println("No routes available because no stops exist.");
            return;
        }

        Set<String> printedEdges = new HashSet<>();
        System.out.println("All Routes:");

        for (String stop : graph.getStops()) {
            for (Edge edge : graph.getNeighbors(stop)) {
                String edgeKey = createEdgeKey(stop, edge.getDestination());
                if (!printedEdges.contains(edgeKey)) {
                    printedEdges.add(edgeKey);
                    System.out.println("  " + stop + " -> " + edge.getDestination() + " (Distance: " + edge.getWeight() + ")");
                }
            }
        }

        if (printedEdges.isEmpty()) {
            System.out.println("  No routes have been added yet.");
        }
    }

    public void showAllStopsAndRoutes() {
        showAllStops();
        System.out.println();
        showAllRoutes();
    }

    public void findShortestPath(String sourceStopId, String destinationStopId) {
        String source = normalize(sourceStopId);
        String destination = normalize(destinationStopId);

        if (!ValidationService.isValidStopId(source) || !ValidationService.isValidStopId(destination)) {
            System.out.println("⚠ Invalid input. Please enter valid stop IDs.");
            return;
        }

        if (!ValidationService.stopExists(stops, source)) {
            System.out.println("❌ Stop does not exist. Please add it first: " + source);
            return;
        }

        if (!ValidationService.stopExists(stops, destination)) {
            System.out.println("❌ Stop does not exist. Please add it first: " + destination);
            return;
        }

        if (!ValidationService.validateGraphHasStops(graph)) {
            System.out.println("❌ No stops are available in the graph. Add stops before searching.");
            return;
        }

        RouteOptimizer.RouteResult result = RouteOptimizer.findShortestPath(graph, source, destination);
        if (result == null || result.getPath().isEmpty()) {
            System.out.println("❌ No path exists between " + source + " and " + destination + ".");
            return;
        }

        printRouteResult(source, destination, result);
    }

    private void printRouteResult(String source, String destination, RouteOptimizer.RouteResult result) {
        System.out.println("========================================");
        System.out.println("        BUS ROUTING SYSTEM (MAP)");
        System.out.println("========================================");
        System.out.println();
        System.out.println("📍 SOURCE: " + source);
        System.out.println("📍 DESTINATION: " + destination);
        System.out.println();
        System.out.println("🛣️  CALCULATING OPTIMAL ROUTE...");
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println("🚏 " + String.join(" → ", result.getPath()));
        System.out.println("📏 TOTAL DISTANCE: " + result.getTotalDistance() + " km");
        System.out.println("----------------------------------------");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String createEdgeKey(String from, String to) {
        return from.compareTo(to) <= 0 ? from + "|" + to : to + "|" + from;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            if (!isNumeric(input)) {
                System.out.println("Invalid selection. Enter a number between 1 and 5.");
                continue;
            }

            int option = Integer.parseInt(input);
            switch (option) {
                case 1:
                    handleAddBusStop(scanner);
                    break;
                case 2:
                    handleAddRoute(scanner);
                    break;
                case 3:
                    showAllStopsAndRoutes();
                    break;
                case 4:
                    handleFindShortestPath(scanner);
                    break;
                case 5:
                    System.out.println("Exiting system. Goodbye.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid selection. Enter a number between 1 and 5.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("        EDUCATIONAL BUS ROUTING CLI");
        System.out.println("========================================");
        System.out.println("1. ➕ Add Bus Stop");
        System.out.println("2. 🔗 Add Route");
        System.out.println("3. 📍 View All Stops and Routes");
        System.out.println("4. 🗺️  Find Shortest Path");
        System.out.println("5. ❌ Exit");
        System.out.print("Choose an option: ");
    }

    private void handleAddBusStop(Scanner scanner) {
        System.out.print("Enter stop ID: ");
        String stopId = scanner.nextLine();
        System.out.print("Enter stop name: ");
        String stopName = scanner.nextLine();
        addBusStop(stopId, stopName);
    }

    private void handleAddRoute(Scanner scanner) {
        System.out.print("Enter origin stop ID: ");
        String from = scanner.nextLine();
        System.out.print("Enter destination stop ID: ");
        String to = scanner.nextLine();
        System.out.print("Enter distance: ");
        String distanceInput = scanner.nextLine();

        if (!isNumeric(distanceInput)) {
            System.out.println("Distance must be a positive integer.");
            return;
        }

        int distance = Integer.parseInt(distanceInput);
        addRoute(from, to, distance);
    }

    private void handleFindShortestPath(Scanner scanner) {
        System.out.print("Enter source stop ID: ");
        String source = scanner.nextLine();
        System.out.print("Enter destination stop ID: ");
        String destination = scanner.nextLine();
        findShortestPath(source, destination);
    }

    private boolean isNumeric(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return value.chars().allMatch(Character::isDigit);
    }
}