import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day14 {
    public static void main(String[] args) {
        calculate("src/day14/example.txt", matrix -> rollNorth(matrix));
        calculate("src/day14/input.txt", matrix -> rollNorth(matrix));
        calculate("src/day14/example.txt", matrix -> fullRoll(matrix));
        calculate("src/day14/input.txt", matrix -> fullRoll(matrix)); 
    }

    private static void calculate(String filename, Consumer<char[][]> roll) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                    .map(String::toCharArray)
                    .toArray(char[][]::new);
            var x_length = matrix[0].length;
            var y_length = matrix.length;
            List<String> matlist = new ArrayList<>();
            List<Integer> sumlist = new ArrayList<>();
            while(true){
                roll.accept(matrix);
                int sum = IntStream.range(0, y_length).map(y -> {
                    var rowsum = (int) IntStream.range(0, x_length).filter(x -> matrix[y][x] == 'O').count();
                    return rowsum * (y_length - y);
                }).sum();
                var matrixAsString = toString(matrix);
                if(matlist.contains(matrixAsString)){
                    break;
                }
                matlist.add(matrixAsString);
                sumlist.add(sum);
            }
            if(sumlist.size() == 1){
                System.out.println(sumlist.get(0));
                return;
            }
            var tail = matlist.indexOf(toString(matrix));
            var cycleLength = matlist.size() - tail;
            var last = (int)(1000000000L - tail) % cycleLength;
            System.out.println(sumlist.get(last + tail - 1));
        });
    }

    private static void fullRoll(char[][] matrix) {
        rollNorth(matrix);
        rollWest(matrix);
        rollSouth(matrix);
        rollEast(matrix);
    }

    private static String toString(char[][] matrix){
        return IntStream.range(0, matrix.length).mapToObj(y -> {
            return new String(matrix[y]);
        }).collect(Collectors.joining("\n"));
    }

    private static void rollNorth(char[][] matrix) {
        var x_length = matrix[0].length;
        var y_length = matrix.length;
        IntStream.range(0, y_length).forEach(source -> {
            IntStream.range(0, x_length)
                    .filter(x -> matrix[source][x] == 'O')
                    .forEach(x -> {
                        var target = IntStream.iterate(source, i -> i - 1)
                                .dropWhile(i -> i > 0 &&
                                        (matrix[i - 1][x] == '.'))
                                .findFirst();
                        target.ifPresent(t -> {
                            if (source != t) {
                                matrix[source][x] = '.';
                                matrix[t][x] = 'O';
                            }
                        });
                    });
        });
    }

    private static void rollSouth(char[][] matrix) {
        var x_length = matrix[0].length;
        var y_length = matrix.length;
        IntStream.range(0, y_length).map(y -> y_length - y - 1).forEach(source -> {
            IntStream.range(0, x_length)
                    .filter(x -> matrix[source][x] == 'O')
                    .forEach(x -> {
                        var target = IntStream.iterate(source, i -> i + 1)
                                .dropWhile(i -> i < y_length - 1 &&
                                        (matrix[i + 1][x] == '.'))
                                .findFirst();
                        target.ifPresent(t -> {
                            if (source != t) {
                                matrix[source][x] = '.';
                                matrix[t][x] = 'O';
                            }
                        });
                    });
        });
    }

    private static void rollWest(char[][] matrix) {
        var x_length = matrix[0].length;
        var y_length = matrix.length;
        IntStream.range(0, x_length).forEach(source -> {
            IntStream.range(0, y_length)
                    .filter(y -> matrix[y][source] == 'O')
                    .forEach(y -> {
                        var target = IntStream.iterate(source, i -> i - 1)
                                .dropWhile(i -> i > 0 &&
                                        (matrix[y][i - 1] == '.'))
                                .findFirst();
                        target.ifPresent(t -> {
                            if (source != t) {
                                matrix[y][source] = '.';
                                matrix[y][t] = 'O';
                            }
                        });
                    });
        });
    }

    private static void rollEast(char[][] matrix) {
        var x_length = matrix[0].length;
        var y_length = matrix.length;
        IntStream.range(0, x_length).map(x -> x_length - x - 1).forEach(source -> {
            IntStream.range(0, y_length)
                    .filter(y -> matrix[y][source] == 'O')
                    .forEach(y -> {
                        var target = IntStream.iterate(source, i -> i + 1)
                                .dropWhile(i -> i < y_length - 1 &&
                                        (matrix[y][i + 1] == '.'))
                                .findFirst();
                        target.ifPresent(t -> {
                            if (source != t) {
                                matrix[y][source] = '.';
                                matrix[y][t] = 'O';
                            }
                        });
                    });
        });
    }
}
