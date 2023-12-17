import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day17 {

    record Node(Coordinates coordinates, int value, Coordinates direction, int steps) {
    }

    record Coordinates(int x, int y) {
        public Coordinates add(Day17.Coordinates direction) {
            return new Coordinates(this.x + direction.x, this.y + direction.y);
        }
    }

    public static void main(String[] args) {
        Predicate<Node> aFilter = node -> node.steps < 3; 
        calculate_a("src/day17/example.txt", aFilter);
        calculate_a("src/day17/input.txt", aFilter);
        calculate_b("src/day17/example.txt");
        calculate_b("src/day17/example2.txt");
        calculate_b("src/day17/input.txt"); 
    }

    private static void calculate_a(String filename, Predicate<Node> filter) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                    .map(line -> Util.split(line, "").map(Integer::parseInt).toArray(Integer[]::new))
                    .toArray(Integer[][]::new);
            var max_x = matrix[0].length;
            var max_y = matrix.length;

            var finalNodes = calculate(matrix, max_x, max_y, node -> getNextNodes_a(node, matrix, max_x, max_y, filter));
            System.out.println(finalNodes.stream().map(Node::value).min(Comparator.naturalOrder()).get());
        });
    }

    private static void calculate_b(String filename) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                    .map(line -> Util.split(line, "").map(Integer::parseInt).toArray(Integer[]::new))
                    .toArray(Integer[][]::new);
            var max_x = matrix[0].length;
            var max_y = matrix.length;

            var finalNodes = calculate(matrix, max_x, max_y, node -> getNextNodes_b(node, matrix, max_x, max_y));
            System.out.println(finalNodes
                .stream()
                .filter(node -> node.steps >= 4)
                .map(Node::value)
                .min(Comparator.naturalOrder()).get());
        });
    }

    private static Set<Day17.Node> calculate(Integer[][] matrix, int max_x, int max_y, Function<Node, Stream<Node>> nextFunction) {
        var visited = IntStream.range(0, max_y)
                .mapToObj(y -> IntStream.range(0, max_x).mapToObj(x -> new HashSet<Node>()).toList()).toList();

        var nodes = List.of(
            new Node(new Coordinates(0, 0), 0, new Coordinates(1, 0), 0),
            new Node(new Coordinates(0, 0), 0, new Coordinates(0, 1), 0)
        );
        while (!nodes.isEmpty()) {
            nodes = nodes.stream()
                    .flatMap(node -> nextFunction.apply(node))
                    .filter(node -> hasNotBeenVisited(node, visited))
                    .peek(node -> visited.get(node.coordinates.y).get(node.coordinates.x).add(node))
                    .sorted(Comparator.comparing(Node::value))
                    .toList();
        }
        return visited.get(max_y - 1).get(max_x - 1);
    }

    private static Stream<Node> getNextNodes_a(Node source, Integer[][] matrix, int max_x, int max_y, Predicate<Node> filter) {
        return Stream.of(
                new Coordinates(-source.direction.y, source.direction.x),
                new Coordinates(source.direction.y, -source.direction.x),
                source.direction).filter(dir -> {
                    var newCoordinates = source.coordinates.add(dir);
                    if (newCoordinates.x < 0 || newCoordinates.x >= max_x)
                        return false;
                    if (newCoordinates.y < 0 || newCoordinates.y >= max_y)
                        return false;
                    return true;
                }).map(direction -> {
                    var newCoordinates = source.coordinates.add(direction);
                    return new Node(
                            newCoordinates,
                            source.value + matrix[newCoordinates.y][newCoordinates.x],
                            direction,
                            direction.equals(source.direction) ? source.steps + 1 : 0);
                }).filter(filter);    
    }

    private static Stream<Node> getNextNodes_b(Node source, Integer[][] matrix, int max_x, int max_y) {
        return Stream.of(
                new Coordinates(-source.direction.y, source.direction.x),
                new Coordinates(source.direction.y, -source.direction.x),
                source.direction).filter(dir -> {
                    var newCoordinates = source.coordinates.add(dir);
                    if (newCoordinates.x < 0 || newCoordinates.x >= max_x)
                        return false;
                    if (newCoordinates.y < 0 || newCoordinates.y >= max_y)
                        return false;
                    return true;
                }).map(direction -> {
                    var newCoordinates = source.coordinates.add(direction);
                    return new Node(
                            newCoordinates,
                            source.value + matrix[newCoordinates.y][newCoordinates.x],
                            direction,
                            direction.equals(source.direction) ? source.steps + 1 : 1);
                })
                .filter(node -> {
                    if(node.direction.equals(source.direction))
                        return node.steps <= 10;
                    if(source.steps < 4)
                        return false;
                    switch (node.direction.x) {
                        case 1:
                            return node.coordinates.x < max_x - 3;
                        case -1:
                            return node.coordinates.x >= 3;
                        default:
                            switch(node.direction.y){
                                case 1:
                                    return node.coordinates.y < max_y - 3;
                                case -1:
                                    return node.coordinates.y >= 3;
                                default:
                                    throw new RuntimeException();
                            }
                    }
                });
    }

    private static boolean hasNotBeenVisited(Node node, List<List<HashSet<Node>>> visited) {
        var vis = visited.get(node.coordinates.y).get(node.coordinates.x);
        var found = vis.stream().filter(v -> v.steps == node.steps && v.direction.equals(node.direction)).findAny();
        return found.map(f -> {
            if (f.value > node.value) {
                vis.remove(f);
                vis.add(node);
                return true;
            }
            return false;
        }).orElse(true);
    }
}
