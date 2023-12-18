import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day18 {

  record Hole(Coordinates coordinates, Type type) {
  }

  record Line(Coordinates start, Coordinates end) {
  }

  enum Type {
    LOOP, INSIDE, OUTSIDE
  }

  record Coordinates(int x, int y) {

    public Coordinates add(Coordinates direction, int times) {
      return new Coordinates(this.x + times * direction.x, this.y + times * direction.y);
    }
  }

  enum Direction {
    U(new Coordinates(0, -1)),
    D(new Coordinates(0, 1)),
    R(new Coordinates(1, 0)),
    L(new Coordinates(-1, 0));

    private final Coordinates coordinates;

    Direction(Coordinates coordinates) {
      this.coordinates = coordinates;
    }

    // 0 means R, 1 means D, 2 means L, and 3 means U.
    static Direction fromHex(int i) {
      return switch (i) {
        case 0 -> R;
        case 1 -> D;
        case 2 -> L;
        case 3 -> U;
        default -> throw new RuntimeException();
      };
    }
  }

  record Instruction(Coordinates direction, int steps) {
  }

  public static void main(String[] args) {
    calculate_a("src/day18/example.txt");
    calculate_a("src/day18/input.txt");
    calculate_b("src/day18/example.txt"); // 952408144115 correct
    calculate_b("src/day18/input.txt"); // 85124524530253 too low
  }

  private static void calculate_a(String filename) {
    Util.applyAndPrint(filename, lines -> {
      var instructions = toInstructions_a(lines);
      return calculate(instructions);
    });
  }

  private static List<Instruction> toInstructions_a(Stream<String> lines) {
    return lines.map(l -> l.strip().split(" "))
        .map(split -> new Instruction(Direction.valueOf(split[0]).coordinates, Integer.parseInt(split[1])))
        .collect(Collectors.toList());
  }

  private static void calculate_b(String filename) {
    Util.applyAndPrint(filename, lines -> {
      var instructions = toInstructions_b(lines);
      return calculate(instructions);
    });
  }

  private static List<Instruction> toInstructions_b(Stream<String> lines) {
    return lines.map(l -> l.strip().split(" "))
        .map(split -> new Instruction(Direction.fromHex(Integer.parseInt(split[2].substring(7, 8))).coordinates,
            Integer.parseInt(split[2].substring(2, 7), 16)))
        .collect(Collectors.toList());
  }

  private static String calculate(List<Instruction> instructions) {
    var lines = toLines(instructions);
    var horizontalLines = lines.stream()
        .filter(line -> line.start.x == line.end.x)
        .map(Day18::normalize)
        .toList();

    var startbreaks = horizontalLines.stream().map(hl -> hl.start.y);
    var endbreaks = horizontalLines.stream().map(hl -> hl.end.y);
    var breaks = Stream.of(startbreaks, endbreaks).flatMap(i -> i).distinct().sorted().collect(Collectors.toList());
    var dividedLines = horizontalLines.stream().flatMap(hl -> divide(hl, breaks)).toList();
    var groupedByBreak = breaks.stream().collect(Collectors.toMap(
        i -> i,
        i -> dividedLines.stream().filter(d -> d.start.y == i).sorted(Comparator.comparing(l -> l.start.x)).toList()));

    var areas = groupedByBreak.keySet().stream().sorted().flatMap(key -> {
      var list = groupedByBreak.get(key);
      var l = list.size();
      return IntStream.range(0, l / 2).boxed().map(i -> {
        var start = list.get(2 * i);
        var end = list.get(2 * i + 1);
        return new Line(new Coordinates(start.start.x, start.start.y), new Coordinates(end.end.x, end.end.y));
      });
    }).toList();

    var overlap = BigInteger.ZERO;
    var area = BigInteger.ZERO;
    for (var a : areas) {
      for (var b : areas) {
        if (!a.equals(b)) {
          if (a.start.y == b.end.y) {
            var end = Math.min(a.end.x, b.end.x);
            var start = Math.max(a.start.x, b.start.x);
            var possibleOverlap = end - start + 1;
            if (possibleOverlap > 0)
              overlap = overlap.subtract(BigInteger.valueOf(possibleOverlap));
          }
        } else {
          area = area.add(BigInteger.valueOf(
              (long) (a.end.x - a.start.x + 1) * (a.end.y - a.start.y + 1)));
        }
      }
    }
    return area.add(overlap).toString();
  }

  private static Line normalize(Line line) {
    if (line.start.y < line.end.y) {
      return new Line(line.start, new Coordinates(line.end.x, line.end.y));
    }
    return new Line(line.end, new Coordinates(line.start.x, line.start.y));
  }

  private static Stream<Line> divide(Line line, List<Integer> divides) {
    var dividesWithinLine = divides.stream().filter(d -> d > line.start.y && d < line.end.y);
    var divided = Stream.of(Stream.of(line.start.y), dividesWithinLine, Stream.of(line.end.y))
        .flatMap(i -> i).sorted()
        .toList();
    return IntStream
        .range(0, divided.size() - 1)
        .mapToObj(d -> new Line(new Coordinates(line.start.x, divided.get(d)),
            new Coordinates(line.end.x, divided.get(d + 1))));
  }

  private static List<Line> toLines(List<Instruction> lines) {
    var lineList = new ArrayList<Line>();
    var current = new Coordinates(0, 0);
    for (Instruction line : lines) {
      var newCurrent = current.add(line.direction, line.steps);
      lineList.add(new Line(current, newCurrent));
      current = newCurrent;
    }
    return lineList;
  }
}
