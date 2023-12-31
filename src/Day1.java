import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day1 {

    private static final Map<String, Integer> INTS = 
        Map.of("one", 1, "two", 2, "three", 3, "four", 4, "five", 5, "six", 6, "seven", 7, "eight", 8, "nine", 9);
    public static void main(String[] args) {
        calculate1("src/day1/example1.txt");
        calculate1("src/day1/input1.txt");
        calculate2("src/day1/example2.txt");
        calculate2("src/day1/test2.txt");
        calculate2("src/day1/input1.txt");
    }

    private static void calculate1(String filename) {
        Util.applyToLines(filename, lines -> {
            var result = lines.map(line -> {
                var integerPattern = Pattern.compile("\\d");
                var matcher = integerPattern.matcher(line);
                var list = matcher.results().toList();
                var first = Integer.parseInt(list.get(0).group());
                var last = Integer.parseInt(list.get(list.size() - 1).group());
                return first * 10 + last;
            }).reduce(0, (a, b) -> a + b);
            System.out.println(result);
        });
    }

    private static void calculate2(String filename) {
        Util.applyToLines(filename, lines -> {
            var result = lines.map(line -> {
                var correctedLine = correctLine(line);
                var integerPattern = Pattern.compile("\\d|" + INTS.keySet().stream().collect(Collectors.joining("|")));
                var matcher = integerPattern.matcher(correctedLine);
                var list = matcher.results().toList();
                var first = getIntFromText(list.get(0).group());
                var last = getIntFromText(list.get(list.size() - 1).group());
                return first * 10 + last;
            }).reduce(0, (a, b) -> a + b);
            System.out.println(result);
        });
    }

    private static final Integer getIntFromText(String textOrInt){
        return INTS.containsKey(textOrInt) ? INTS.get(textOrInt) : Integer.parseInt(textOrInt);
    }

    /*
     * ew
     */
    private static final String correctLine(String line){
        return line.replaceAll("oneight", "oneeight")
            .replaceAll("threeight", "threeeight")
            .replaceAll("fiveight", "fiveeight")
            .replaceAll("nineight", "nineeight")
            .replaceAll("twone", "twoone")
            .replaceAll("sevenine", "sevennine")
            .replaceAll("eightwo", "eighttwo")
            .replaceAll("eighthree", "eightthree");
    }
}
