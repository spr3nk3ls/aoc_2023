import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
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
        Set<Character> allPossible = Stream.of(Direction.values()).flatMap(d -> d.possible().stream()).collect(Collectors.toSet());
        calculate("src/day23/example.txt", (dir, ch) -> dir.possible().contains(ch));
        calculate("src/day23/input.txt", (dir, ch) -> dir.possible().contains(ch));
        calculate("src/day23/example.txt", (dir, ch) -> allPossible.contains(ch));
        calculate("src/day23/input.txt", (dir, ch) -> allPossible.contains(ch));
    }

    private static void calculate(String filename, BiPredicate<Direction, Character> pred) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                    .map(line -> line.toCharArray())
                    .toArray(size -> new char[size][]);
            List<Cd> firstSteps = new ArrayList<>(List.of(new Cd(1, 0), new Cd(1, 1)));
            var currentRoutes = new HashSet<List<Cd>>();
            var finishedRoutes = new HashSet<List<Cd>>();
            currentRoutes.add(firstSteps);
            while (!currentRoutes.isEmpty()) {
                var newRoutes = new HashSet<List<Cd>>();
                for (var route : currentRoutes) {
                    if (route.get(route.size() - 1).equals(new Cd(matrix[0].length - 2, matrix.length - 1))) {
                        finishedRoutes.add(route);
                        continue;
                    }
                    var possible = possibleSteps(route, matrix, pred);
                    if (possible.size() == 3) {
                        var newRoute = new ArrayList<>(route);
                        newRoute.add(possible.get(2));
                        newRoutes.add(newRoute);
                    }
                    if (possible.size() > 1) {
                        var newRoute = new ArrayList<>(route);
                        newRoute.add(possible.get(1));
                        newRoutes.add(newRoute);
                    }
                    if (possible.size() > 0) {
                        route.add(possible.get(0));
                        newRoutes.add(route);
                    }
                }
                currentRoutes = newRoutes;
            }
            System.out.println(finishedRoutes.stream().map(r -> r.size()).max(Comparator.naturalOrder()).get() - 1);
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
}
