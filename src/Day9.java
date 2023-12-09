import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day9 {
   
    public static void main(String[] args) {
        calculate("src/day9/example.txt", array -> array[array.length -  1], 1);
        calculate("src/day9/input.txt", array -> array[array.length -  1], 1);
        calculate("src/day9/example.txt", array -> array[0], -1);
        calculate("src/day9/input.txt", array -> array[0], -1);
    }

    private static void calculate(String filename, Function<Integer[], Integer> firstOrLast, int parity) {
        Util.applyAndPrint(filename, lines -> {
            return lines.map(line -> {
                var split = Util.split(line, " ").mapToInt(Integer::parseInt).boxed().toArray(size -> new Integer[size]);
                return getDiffs(split, (int)firstOrLast.apply(split), firstOrLast, parity, parity);
            }).reduce(0, (a, b) -> a+b).toString();
        });
    }

    private static Integer getDiffs(Integer[] original, int number, Function<Integer[], Integer> firstOrLast, int sw, int parity){
        var oneLower = IntStream.range(0, original.length - 1)
            .map(i -> original[i + 1] - original[i])
            .mapToObj(Integer::valueOf)
            .toArray(size -> new Integer[size]);
        var last = firstOrLast.apply(oneLower);
        return Arrays.stream(oneLower).allMatch(i -> i==0) ? number : 
            getDiffs(oneLower, number + sw*last, firstOrLast, parity*sw, parity);
    }
}