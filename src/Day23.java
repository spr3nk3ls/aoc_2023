import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day23 {

    record Cd(int x, int y) {
        public Cd add(Cd direction) {
            return new Cd(this.x + direction.x, this.y + direction.y);
        }
    }

    enum Direction {
        N(new Cd(0, -1), List.of('.', '^')),
        S(new Cd(0, 1), List.of('.', 'v')),
        E(new Cd(1, 0), List.of('.', '>')),
        W(new Cd(-1, 0), List.of('.', '<'));

        private final Cd cd;
        private final List<Character> possible;

        Direction(Cd cd, List<Character> possible) {
            this.cd = cd;
            this.possible = possible;
        }

        public Cd cd() {
            return cd;
        }

        public List<Character> possible() {
            return possible;
        }
    }

    public static void main(String[] args) {
        Set<Character> allPossible = Stream.of(Direction.values()).flatMap(d -> d.possible().stream())
                .collect(Collectors.toSet());
        calculate_a("src/day23/example.txt", (dir, ch) -> dir.possible().contains(ch));
        calculate_a("src/day23/input.txt", (dir, ch) -> dir.possible().contains(ch));
        calculate_b("src/day23/example.txt", (dir, ch) -> allPossible.contains(ch));
        calculate_b("src/day23/input.txt", (dir, ch) -> allPossible.contains(ch));
    }

    private static void calculate_a(String filename, BiPredicate<Direction, Character> pred) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                    .map(line -> line.toCharArray())
                    .toArray(size -> new char[size][]);
            List<Cd> firstSteps = new ArrayList<>(List.of(new Cd(1, 0), new Cd(1, 1)));
            var finishedRoutes = new HashSet<Integer>();
            var routes = new Stack<List<Cd>>();
            routes.push(firstSteps);
            while (!routes.isEmpty()) {
                var route = routes.pop();
                while (route != null) {
                    if (route.get(route.size() - 1).equals(new Cd(matrix[0].length - 2, matrix.length - 1))) {
                        finishedRoutes.add(route.size() - 1);
                        route = null;
                        break;
                    }
                    var possible = possibleSteps(route, matrix, pred);
                    if (possible.size() == 3) {
                        var newRoute = new ArrayList<>(route);
                        newRoute.add(possible.get(2));
                        routes.push(newRoute);
                    }
                    if (possible.size() > 1) {
                        var newRoute = new ArrayList<>(route);
                        newRoute.add(possible.get(1));
                        routes.push(newRoute);
                    }
                    if (possible.size() > 0) {
                        route.add(possible.get(0));
                    }
                    if (possible.size() == 0) {
                        route = null;
                    }
                }
            }
            System.out.println(finishedRoutes.stream().max(Comparator.naturalOrder()).get());
        });
    }

    private static List<Cd> possibleSteps(List<Cd> route, char[][] matrix, BiPredicate<Direction, Character> pred) {
        Cd current = route.get(route.size() - 1);
        return Arrays.stream(Direction.values())
                .filter(dir -> pred.test(dir, fromMatrix(dir.cd().add(current), matrix)))
                .map(dir -> dir.cd().add(current))
                .filter(newPos -> !route.contains(newPos))
                .toList();
    }

    private static Character fromMatrix(Cd cd, char[][] matrix) {
        return matrix[cd.y][cd.x];
    }
    
    // Part b starts here

    record Node(Cd middle, List<Cd> connections, Map<Node, Integer> distance) {
        @Override
        public boolean equals(Object other) {
            if (other instanceof Node)
                return this.middle.equals(((Node) other).middle);
            return false;
        }

        @Override
        public int hashCode() {
            return middle.hashCode();
        }
    }

    private static void calculate_b(String filename, BiPredicate<Direction, Character> pred) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                    .map(line -> line.toCharArray())
                    .toArray(size -> new char[size][]);
            var nodes = getNodes(matrix);
            var start = new Node(new Cd(1, 0), new ArrayList<>(List.of(new Cd(1, 1))), new HashMap<>());
            var end = new Node(
                    new Cd(matrix[0].length - 2, matrix.length - 1),
                    new ArrayList<>(List.of(new Cd(matrix[0].length - 2, matrix.length - 2))),
                    new HashMap<>());
            nodes.put(start.middle, start);
            nodes.put(end.middle, end);
            connectNodes(nodes.values(), matrix, pred);
            var routes = getRoutes(start, end, nodes);
            var lengths = getLengths(routes);
            System.out.println(lengths.stream().max(Comparator.naturalOrder()).get());
        });
    }

    private static Map<Cd, Node> getNodes(char[][] matrix) {
        var list = IntStream.range(1, matrix.length - 1).boxed()
                .flatMap(y -> IntStream.range(1, matrix[y].length - 1).mapToObj(x -> {
                    if (matrix[y][x] == '.') {
                        var connections = Stream
                                .of(new Cd(x, y - 1), new Cd(x, y + 1), new Cd(x - 1, y), new Cd(x + 1, y))
                                .filter(cd -> matrix[cd.y][cd.x] != '#').toList();
                        if (connections.size() > 2)
                            return new Node(new Cd(x, y), connections, new HashMap<>());
                    }
                    return null;
                })).filter(i -> i != null).toList();
        return list.stream().collect(Collectors.toMap(i -> i.middle, i -> i));
    }

    private static void connectNodes(Collection<Day23.Node> collection, char[][] matrix,
            BiPredicate<Direction, Character> pred) {
        for (var node : collection) {
            for (var connection : node.connections) {
                var firstSteps = new ArrayList<>(List.of(node.middle, connection));
                while (true) {
                    var possible = possibleSteps(firstSteps, matrix, pred);
                    if (possible.size() != 1) {
                        throw new RuntimeException();
                    }
                    var otherNode = collection.stream().filter(n -> n.middle.equals(possible.get(0))).findAny();
                    if (otherNode.isPresent()) {
                        node.distance.put(otherNode.get(), firstSteps.size());
                        break;
                    }
                    firstSteps.add(possible.get(0));
                }
            }
        }
    }

    private static Set<List<Day23.Node>> getRoutes(Node start, Node end, Map<Cd, Node> nodes){
        var finished = new HashSet<List<Node>>();
        var routes = new Stack<List<Node>>();
        routes.add(new ArrayList<>(List.of(start)));
        while (!routes.isEmpty()) {
            var route = routes.pop();
            while (true) {
                if(route.get(route.size() - 1).equals(end)){
                    finished.add(route);
                    break;
                }
                var middles = route.stream().map(r -> r.middle()).toList();
                var connect = route.get(route.size() - 1).distance.keySet().stream()
                    .map(c -> c.middle)
                    .filter(m -> !middles.contains(m))
                    .toList();
                if (connect.size() == 3) {
                    var newRoute = new ArrayList<>(route);
                    newRoute.add(nodes.get(connect.get(2)));
                    routes.add(newRoute);
                }
                if (connect.size() > 1) {
                    var newRoute = new ArrayList<>(route);
                    newRoute.add(nodes.get(connect.get(1)));
                    routes.add(newRoute);
                }
                if (connect.size() > 0) {
                    route.add(nodes.get(connect.get(0)));
                } else {
                    break;
                }
            }
        }
        return finished;
    }

    private static Set<Integer> getLengths(Set<List<Node>> nodes){
        var result = new HashSet<Integer>();
        for(var nodeList : nodes){
            int length = 0;
            for(int i = 0; i < nodeList.size() -1; i++){
                length += nodeList.get(i).distance.get(nodeList.get(i+1));
            }
            result.add(length);
        }
        return result;
    }
}
