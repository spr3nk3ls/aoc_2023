import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day16 {

    public static void main(String[] args) {
        calculate_a("src/day16/example.txt");
        calculate_a("src/day16/input.txt");
        calculate_b("src/day16/example.txt");
        calculate_b("src/day16/input.txt");
    }

    private static void calculate_a(String filename) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                .map(String::toCharArray)
                .toArray(char[][]::new);
            var result = calculate(matrix, new Beam(0, 0, Direction.E));
            System.out.println(result);
        });
    }

    private static void calculate_b(String filename) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                .map(String::toCharArray)
                .toArray(char[][]::new);
            var x_length = matrix[0].length;
            var y_length = matrix.length;
            var result = Stream.of(
                IntStream.range(0, x_length).mapToObj(x -> new Beam(x, 0, Direction.S)),
                IntStream.range(0, x_length).mapToObj(x -> new Beam(x, y_length - 1, Direction.N)),
                IntStream.range(0, y_length).mapToObj(y -> new Beam(0, y, Direction.E)),
                IntStream.range(0, y_length).mapToObj(y -> new Beam(x_length - 1, y, Direction.W))
            ).flatMap(b -> b).map(beam -> calculate(matrix, beam)).max(Integer::compare);
            System.out.println(result.get());
        });
    }

    private static int calculate(char[][] matrix, Beam initialBeam) {
        var x_length = matrix[0].length;
        var y_length = matrix.length;
        var result = new char[y_length][x_length];
        var beams = new ArrayList<Beam>();
        var allBeams = new ArrayList<Beam>();
        beams.add(initialBeam);
        allBeams.add(initialBeam);
        while (!beams.isEmpty()) {
            var newBeams = new ArrayList<Beam>();
            for (int i = 0; i < beams.size(); i++) {
                var beam = beams.get(i);
                result[beam.y][beam.x] = '#';
                var splittedBeams = getNewBeams(beam, matrix)
                        .filter(b -> b.x >= 0 && b.x < x_length)
                        .filter(b -> b.y >= 0 && b.y < y_length)
                        .filter(b -> !allBeams.contains(b))
                        .toList();
                newBeams.addAll(splittedBeams);
                allBeams.addAll(splittedBeams);
            }
            beams = newBeams;
        }
        var resultSum = IntStream.range(0, y_length)
                .map(y -> (int) IntStream.range(0, x_length).filter(x -> result[y][x] == '#').count()).sum();
        return resultSum;
    }

    private static Stream<Beam> getNewBeams(Day16.Beam beam, char[][] matrix) {
        var point = matrix[beam.y][beam.x];
        if (point == '.' || splitAtRightAngle(beam.direction, point))
            return Stream.of(new Beam(beam.x + beam.direction.x, beam.y + beam.direction.y, beam.direction));
        if (point == '/' || point == '\\') {
            var newDirection = Direction.bounce(beam.direction, point);
            return Stream.of(new Beam(beam.x + newDirection.x, beam.y + newDirection.y, newDirection));
        }
        if (point == '|')
            return Stream.of(new Beam(beam.x, beam.y - 1, Direction.N), new Beam(beam.x, beam.y + 1, Direction.S));
        if (point == '-')
            return Stream.of(new Beam(beam.x + 1, beam.y, Direction.E), new Beam(beam.x - 1, beam.y, Direction.W));
        throw new RuntimeException();
    }

    private static boolean splitAtRightAngle(Day16.Direction direction, char point) {
        if (point == '|' && (direction == Direction.N || direction == Direction.S))
            return true;
        if (point == '-' && (direction == Direction.E || direction == Direction.W))
            return true;
        return false;
    }

    record Beam(int x, int y, Direction direction) {
    }

    enum Direction {
        N(0, -1), S(0, 1), E(1, 0), W(-1, 0);

        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        static Direction bounce(Direction direction, char mirror) {
            if (mirror == '/') {
                switch (direction) {
                    case E:
                        return N;
                    case W:
                        return S;
                    case N:
                        return E;
                    case S:
                        return W;
                    default:
                        throw new RuntimeException();
                }
            } else if (mirror == '\\') {
                switch (direction) {
                    case E:
                        return S;
                    case W:
                        return N;
                    case N:
                        return W;
                    case S:
                        return E;
                    default:
                        throw new RuntimeException();
                }
            }
            throw new RuntimeException();
        }
    }
}
