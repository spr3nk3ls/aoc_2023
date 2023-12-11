import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Day11 {

  record Galaxy(BigInteger x, BigInteger y) {
  }

  public static void main(String[] args) {
    calculate("src/day11/example.txt", 2);
    calculate("src/day11/input.txt", 1);
    calculate("src/day11/example.txt", 10);
    calculate("src/day11/example.txt", 100);
    calculate("src/day11/input.txt", 1000000);
  }

  public static void calculate(String filename, int expansion) {
    Util.applyToFile(filename, asString -> {
      var matrix = Util.split(asString, "\n")
          .map(String::toCharArray)
          .toArray(char[][]::new);
      var x_length = matrix[0].length;
      var y_length = matrix.length;

      var galaxies = IntStream.range(0, y_length).boxed()
          .flatMap(y -> IntStream.range(0, x_length).filter(x -> matrix[y][x] == '#')
              .mapToObj(x -> new Galaxy(BigInteger.valueOf(x), BigInteger.valueOf(y))))
          .toArray(Galaxy[]::new);

      var expansionDiff = BigInteger.valueOf(expansion - 1);

      var x_coor = Arrays.stream(galaxies).map(g -> g.x).toList();
      IntStream.iterate(x_length - 1, i -> i - 1)
          .limit(x_length)
          .filter(x -> !x_coor.contains(BigInteger.valueOf(x)))
          .forEach(x -> {
            for (int i = 0; i < galaxies.length; i++) {
              if (galaxies[i].x.compareTo(BigInteger.valueOf(x)) > 0)
                galaxies[i] = new Galaxy(galaxies[i].x.add(expansionDiff), galaxies[i].y);
            }
          });

      var y_coor = Arrays.stream(galaxies).map(g -> g.y).toList();
      IntStream.iterate(y_length, i -> i - 1)
          .limit(y_length)
          .filter(y -> !y_coor.contains(BigInteger.valueOf(y)))
          .forEach(y -> {
            for (int i = 0; i < galaxies.length; i++) {
              if (galaxies[i].y.compareTo(BigInteger.valueOf(y)) > 0)
                galaxies[i] = new Galaxy(galaxies[i].x, galaxies[i].y.add(expansionDiff));
            }
          });

      var result = Arrays.stream(galaxies)
          .flatMap(g1 -> Arrays.stream(galaxies).map(g2 -> g1.x.subtract(g2.x).abs().add(g1.y.subtract(g2.y).abs())))
          .reduce(BigInteger.ZERO, BigInteger::add);
      System.out.println(result.divide(BigInteger.TWO));
    });
  }
}
