import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day10 {

    record Pointer(int x, int y, Direction from) {}
    enum Direction {
        N(0, -1), S(0, 1), E(1, 0), W(-1, 0);
        private final int x;
        private final int y;
        Direction(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void apply(T t, U u, V v);
    }

    public static void main(String[] args) {
        calculate("src/day10/example.txt", Direction.W, 'F', 'I');
        calculate("src/day10/example2.txt", Direction.W, 'F', 'O');
        calculate("src/day10/input.txt", Direction.S, 'J', 'I');
    }

    private static void calculate(String filename, Direction sFrom, char sValue, char innerChar) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                .map(line -> line.toCharArray())
                .toArray(size -> new char[size][]);
            var start = IntStream.range(0, matrix.length)
                .mapToObj(y -> IntStream.range(0, matrix[y].length)
                    .filter(x -> matrix[y][x] == 'S')
                    .mapToObj(x -> new Pointer(x, y, sFrom))
                ).flatMap(s -> s).findAny().orElseThrow();
            matrix[start.y][start.x] = sValue;
            var pointers = new ArrayList<Pointer>();
            pointers.add(getNewDirection(start, matrix));
            while(pointers.getLast().x != start.x || pointers.getLast().y != start.y)
                pointers.add(getNewDirection(pointers.getLast(), matrix));
            System.out.println(pointers.size()/2);

            notInLoop(matrix, pointers);
            markAdjacent(matrix, pointers);
            while(count(matrix, 'N') > 0)
                fillInIO(matrix);
            System.out.println(count(matrix, innerChar));
        });
    }

    private static void notInLoop(char[][] matrix, List<Day10.Pointer> pointers) {
        IntStream.range(0, matrix.length).forEach(y -> {
            IntStream.range(0, matrix[y].length).forEach(x -> {
                if(pointers.stream().noneMatch(p -> p.x == x && p.y == y))
                    matrix[y][x] = 'N';
            });
        });
    }

    private static void markAdjacent(char[][] matrix, List<Day10.Pointer> pointers) {
        pointers.stream().forEach(pointer -> {
            var value = matrix[pointer.y][pointer.x];
            if (pointer.from == Direction.E && (value == '-' || value == 'J' || value == '7')){
                if (pointer.y > 0 && matrix[pointer.y - 1][pointer.x] == 'N')
                    matrix[pointer.y - 1][pointer.x] = 'I';
                if (pointer.y < matrix.length - 1 && matrix[pointer.y + 1][pointer.x] == 'N')
                    matrix[pointer.y + 1][pointer.x] = 'O';
                if (pointer.x < matrix[0].length - 1 && matrix[pointer.y][pointer.x + 1] == 'N'){
                    if(value == 'J')
                        matrix[pointer.y][pointer.x + 1] = 'O';
                    if(value == '7')
                        matrix[pointer.y][pointer.x + 1] = 'I';
                }
            }
            if (pointer.from == Direction.W && (value == '-' || value == 'F' || value == 'L')){
                if (pointer.y > 0 && matrix[pointer.y - 1][pointer.x] == 'N')
                    matrix[pointer.y - 1][pointer.x] = 'O';
                if (pointer.y < matrix.length - 1 && matrix[pointer.y + 1][pointer.x] == 'N')
                    matrix[pointer.y + 1][pointer.x] = 'I';
            }
            if (pointer.from == Direction.S && (value == '|' || value == 'J' || value == 'L')){
                if (pointer.x > 0 && matrix[pointer.y][pointer.x - 1] == 'N')
                    matrix[pointer.y][pointer.x - 1] = 'O';
                if (pointer.x < matrix[0].length - 1 && matrix[pointer.y][pointer.x + 1] == 'N')
                    matrix[pointer.y][pointer.x + 1] = 'I';
            }
            if (pointer.from == Direction.N && (value == '|' || value == '7' || value == 'F')){
                if (pointer.x > 0 && matrix[pointer.y][pointer.x - 1] == 'N')
                    matrix[pointer.y][pointer.x - 1] = 'I';
                if (pointer.x < matrix[0].length - 1 && matrix[pointer.y][pointer.x + 1] == 'N')
                    matrix[pointer.y][pointer.x + 1] = 'O';
            }
        });
    }

    private static void fillInIO(char[][] matrix){
        IntStream.range(0, matrix.length).forEach(y -> {
            IntStream.range(0, matrix[y].length).forEach(x -> {
                if(matrix[y][x] == 'N'){
                    var adjacent = getAdjacent(matrix, x, y);
                    if(Arrays.stream(adjacent).filter(c -> c != null).anyMatch(c -> c == 'O')){
                        matrix[y][x] = 'O';
                    }
                    if(Arrays.stream(adjacent).filter(c -> c != null).anyMatch(c -> c == 'I')){
                        matrix[y][x] = 'I';
                    }
                }
            });
        });
    }

    private static int count(char[][] matrix, char c){
        return IntStream.range(0, matrix.length)
            .map(y -> (int)IntStream.range(0, matrix[y].length).filter(x -> matrix[y][x] == c).count()
        ).sum();
    }

    private static Character[] getAdjacent(char[][] matrix, int x, int y){
        return new Character[]{
            y > 0 && x > 0 ? matrix[y-1][x-1] : null,
            y > 0 ? matrix[y-1][x] : null,
            x > 0 ? matrix[y][x-1] : null,
            y < matrix.length - 1 ? matrix[y+1][x] : null,
            y < matrix.length - 1 && x < matrix[0].length - 1 ? matrix[y+1][x+1] : null,
            x < matrix[0].length - 1 ? matrix[y][x+1] : null
        };

    }

    private static Pointer getNewDirection(Day10.Pointer pointer, char[][] matrix) {
        var tile = matrix[pointer.y][pointer.x];
        var to = getTo(tile, pointer.from);
        return new Pointer(pointer.x + to.x, pointer.y + to.y, to);
    }

    private static Direction getTo(char tile, Day10.Direction from) {
        if(from == Direction.S){
            if(tile == '|')
                return Direction.S;
            if(tile == 'L')
                return Direction.E;
            if(tile == 'J')
                return Direction.W;
            throw new RuntimeException();
        }
        if(from == Direction.E){
            if(tile == '-')
                return Direction.E;
            if(tile == '7')
                return Direction.S;
            if(tile == 'J')
                return Direction.N;
            throw new RuntimeException();
        }
        if(from == Direction.N){
            if(tile == '|')
                return Direction.N;
            if(tile == '7')
                return Direction.W;
            if(tile == 'F')
                return Direction.E;
            throw new RuntimeException();
        }
        if(from == Direction.W){
            if(tile == '-')
                return Direction.W;
            if(tile == 'L')
                return Direction.N;
            if(tile == 'F')
                return Direction.S;
            throw new RuntimeException();
        }
        throw new RuntimeException();
    }
}
