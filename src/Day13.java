import java.util.Iterator;
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
                .filter(x -> rowsEqual(matrix, x, y_length, x_length))
                .findAny().orElse(-1);
        if (x_mirror != -1)
            return x_mirror + 1;

        var y_mirror = IntStream.range(0, y_length - 1)
                .filter(y -> colsEqual(matrix, y, x_length, y_length))
                .findAny().orElse(-1);
        if (y_mirror != -1)
            return 100 * (y_mirror + 1);

        return 0;
    }

    private static boolean colsEqual(char[][] matrix, int index, int line_length, int mat_length) {
        var it1 = IntStream.iterate(0, i -> i + 1)
                .takeWhile(i -> index - i >= 0)
                .boxed()
                .flatMap(i -> IntStream.range(0, line_length).mapToObj(x -> matrix[index - i][x]))
                .iterator();
        var it2 = IntStream.iterate(0, i -> i + 1)
                .takeWhile(i -> index + i + 1 < mat_length)
                .boxed()
                .flatMap(i -> IntStream.range(0, line_length).mapToObj(x -> matrix[index + i + 1][x]))
                .iterator();
        return itsEqual(it1, it2);
    }

    private static boolean rowsEqual(char[][] matrix, int index, int line_length, int mat_length) {
        var it1 = IntStream.iterate(0, i -> i + 1)
                .takeWhile(i -> index - i >= 0)
                .boxed()
                .flatMap(i -> IntStream.range(0, line_length).mapToObj(y -> matrix[y][index - i]))
                .iterator();
        var it2 = IntStream.iterate(0, i -> i + 1)
                .takeWhile(i -> index + i + 1 < mat_length)
                .boxed()
                .flatMap(i -> IntStream.range(0, line_length).mapToObj(y -> matrix[y][index + i + 1]))
                .iterator();
        return itsEqual(it1, it2);
    }

    /*
     * Part one
     */
    // private static boolean itsEqual(Iterator<Character> one, Iterator<Character>
    // two) {
    // while (one.hasNext() && two.hasNext())
    // if (one.next() != two.next())
    // return false;
    // return true;
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
