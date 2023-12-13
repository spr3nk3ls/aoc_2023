import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class Day13 {
    public static void main(String[] args) {
        calculate("src/day13/example.txt");
        calculate("src/day13/input.txt");
    }

    private static void calculate(String filename) {
        Util.applyToFile(filename, asString -> {
            var sum = Util.split(asString, "\n\n")
                    .map(Day13::calculateForMatrix)
                    .reduce(0, (a, b) -> a + b);
            System.out.println(sum);
        });
    }

    private static int calculateForMatrix(String matrixAsString) {
        var matrix = Util.split(matrixAsString, "\n")
                .map(String::toCharArray)
                .toArray(char[][]::new);

        var x_length = matrix[0].length;
        var y_length = matrix.length;

        var x_mirror = IntStream.range(0, x_length - 1)
                .filter(x -> linesEqual(matrix, x, y_length, x_length, (y, i) -> matrix[y][i]))
                .findAny().orElse(-1);
        if (x_mirror != -1)
            return x_mirror + 1;

        var y_mirror = IntStream.range(0, y_length - 1)
                .filter(y -> linesEqual(matrix, y, x_length, y_length, (x, i) -> matrix[i][x]))
                .findAny().orElse(-1);
        if (y_mirror != -1)
            return 100 * (y_mirror + 1);

        return 0;
    }

    private static boolean linesEqual(char[][] matrix, int index, int line_length, int mat_length,
            BiFunction<Integer, Integer, Character> matFunction) {
        var it1 = IntStream.iterate(0, i -> i + 1)
                .takeWhile(i -> index - i >= 0)
                .boxed()
                .flatMap(i -> IntStream.range(0, line_length).mapToObj(x -> matFunction.apply(x, index - i)))
                .iterator();
        var it2 = IntStream.iterate(0, i -> i + 1)
                .takeWhile(i -> index + i + 1 < mat_length)
                .boxed()
                .flatMap(i -> IntStream.range(0, line_length).mapToObj(x -> matFunction.apply(x, index + i + 1)))
                .iterator();
        return itsEqual(it1, it2);
    }

    /*
     * Part one
     */
    // private static boolean itsEqual(Iterator<Character> one, Iterator<Character> two) {
    //     while (one.hasNext() && two.hasNext())
    //         if (one.next() != two.next())
    //             return false;
    //     return true;
    // }

    /*
     * Part two
     */
    private static boolean itsEqual(Iterator<Character> one, Iterator<Character> two) {
        int imperfections = 0;
        while (one.hasNext() && two.hasNext()) {
            if (imperfections > 1)
                return false;
            if (one.next() != two.next())
                imperfections++;
        }
        if (imperfections != 1)
            return false;
        return true;
    }
}
