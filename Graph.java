import java.util.*;

public class Graph {
    private final Map<String, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addStop(String stop) {
        adjacencyList.putIfAbsent(stop, new ArrayList<>());
    }

    public void addRoute(String from, String to, int weight) {
        addStop(from);
        addStop(to);

        adjacencyList.get(from).add(new Edge(to, weight));
        adjacencyList.get(to).add(new Edge(from, weight));
    }

    public boolean routeExists(String from, String to) {
        if (from == null || to == null) {
            return false;
        }

        if (adjacencyList.containsKey(from)) {
            boolean exists = adjacencyList.get(from).stream()
                    .anyMatch(edge -> edge.getDestination().equals(to));
            if (exists) {
                return true;
            }
        }

        if (adjacencyList.containsKey(to)) {
            return adjacencyList.get(to).stream()
                    .anyMatch(edge -> edge.getDestination().equals(from));
        }

        return false;
    }

    public List<Edge> getNeighbors(String stop) {
        return adjacencyList.getOrDefault(stop, Collections.emptyList());
    }

    public boolean hasStop(String stop) {
        return adjacencyList.containsKey(stop);
    }

    public Set<String> getStops() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }
}

class Edge {
    private final String destination;
    private final int weight;

    public Edge(String destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public String getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }
}