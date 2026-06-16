import java.util.*;

public class RouteOptimizer {

    public static RouteResult findShortestPath(Graph graph, String source, String destination) {
        if (source == null || destination == null || !graph.hasStop(source) || !graph.hasStop(destination)) {
            return null;
        }

        Map<String, Integer> distanceMap = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();

        for (String stop : graph.getStops()) {
            distanceMap.put(stop, Integer.MAX_VALUE);
            parentMap.put(stop, null);
        }

        distanceMap.put(source, 0);
        queue.offer(new Node(source, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.distance > distanceMap.get(current.stopId)) {
                continue;
            }

            for (Edge neighbor : graph.getNeighbors(current.stopId)) {
                int tentativeDistance = current.distance + neighbor.getWeight();
                if (tentativeDistance < distanceMap.get(neighbor.getDestination())) {
                    distanceMap.put(neighbor.getDestination(), tentativeDistance);
                    parentMap.put(neighbor.getDestination(), current.stopId);
                    queue.offer(new Node(neighbor.getDestination(), tentativeDistance));
                }
            }
        }

        int totalDistance = distanceMap.get(destination);
        if (totalDistance == Integer.MAX_VALUE) {
            return null;
        }

        List<String> path = reconstructPath(parentMap, destination);
        return new RouteResult(path, totalDistance);
    }

    private static List<String> reconstructPath(Map<String, String> parentMap, String destination) {
        List<String> path = new ArrayList<>();
        String current = destination;

        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    public static class RouteResult {
        private final List<String> path;
        private final int totalDistance;

        public RouteResult(List<String> path, int totalDistance) {
            this.path = Collections.unmodifiableList(new ArrayList<>(path));
            this.totalDistance = totalDistance;
        }

        public List<String> getPath() {
            return path;
        }

        public int getTotalDistance() {
            return totalDistance;
        }
    }

    private static class Node implements Comparable<Node> {
        private final String stopId;
        private final int distance;

        public Node(String stopId, int distance) {
            this.stopId = stopId;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }
}