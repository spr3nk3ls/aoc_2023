import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day3 {

    record PartValue(int value, int line, int start, int end) {}
    record PartIndicator(int line, int start) {}

    public static void main(String[] args) {
        calculate1("src/day3/example.txt"); 
        calculate1("src/day3/input.txt"); 
        calculate2("src/day3/example.txt");
        calculate2("src/day3/input.txt");
    }

    private static void calculate1(String filename) {
        Util.applyAndPrint(filename, lines -> {
            var lineList = lines.collect(Collectors.toList());
            return Integer.toString(toPartValues(lineList, false));
        });
    }

    private static void calculate2(String filename) {
        Util.applyAndPrint(filename, lines -> {
            var lineList = lines.collect(Collectors.toList());
            return Integer.toString(toPartValues(lineList, true));
        });
    }

    private static int toPartValues(List<String> lines, boolean gearsOnly){
        var values = new HashSet<PartValue>();
        var indicators = new HashSet<PartIndicator>();
        IntStream.range(0, lines.size()).forEach(i -> fillParts(values, indicators, lines.get(i), i, gearsOnly));
        return indicators.stream()
            .map(ind -> getPartValue(ind, values))
            .filter(t -> !gearsOnly || t.size() > 1)
            .map(t -> gearsOnly ? t.stream().reduce(1, (a, b) -> a*b) : t.stream().reduce(0, (a, b) -> a+b))
            .reduce(0, (a, b) -> a + b);
    }

    private static void fillParts(Set<PartValue> values, Set<PartIndicator> indicators, String line, int lineNumber, boolean gearsOnly){
        var i = 0;
        while (i < line.length()) {
            var c = line.charAt(i);
            try {
                Integer.parseInt(String.valueOf(c));
                var value = line.substring(i,line.length()).split("\\D")[0];
                values.add(new PartValue(Integer.parseInt(value), lineNumber, i, i + value.length()));
                i += value.length();
            } catch (NumberFormatException e){
                if(c != '.' && (!gearsOnly || c == '*'))
                    indicators.add(new PartIndicator(lineNumber, i));
                i++;
            }
        }
    }

    private static List<Integer> getPartValue(Day3.PartIndicator ind, Set<Day3.PartValue> values) {
        return values.stream()
            .filter(v -> Math.abs(ind.line - v.line) <= 1)
            .filter(v -> (ind.start - v.start) >= -1 && (ind.start - v.end) <= 0)
            .map(v -> v.value)
            .toList();
    }
}
