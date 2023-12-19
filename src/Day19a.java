import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19a {

    enum Result {
        A, R
    }

    @FunctionalInterface
    interface Rule {
        Function<Map<String, Integer>, Result> apply(Map<String, Rule> system);
    }

    public static void main(String[] args) {
        calculate_a("src/day19/example.txt");
        calculate_a("src/day19/input.txt");
    }

    private static void calculate_a(String filename) {
        Util.applyToFile(filename, lines -> {
            var split = lines.split("\n\n");
            var system = getSystem(Util.split(split[0].strip(), "\n"));
            var parts = getParts(Util.split(split[1].strip(), "\n"));
            var start = system.get("in");
            var result = parts
                    .filter(part -> start.apply(getSystem(Util.split(split[0].strip(), "\n"))).apply(part)
                            .equals(Result.A))
                    .map(part -> part.values().stream().reduce(0, (a, b) -> a + b))
                    .reduce(0, (a, b) -> a + b);
            System.out.println(result);
        });
    }

    private static Stream<Map<String, Integer>> getParts(Stream<String> split) {
        return split.map(s -> s.strip())
                .map(s -> s.substring(1, s.length() - 1))
                .map(s -> s.split(","))
                .map(Day19a::toPart);
    }

    private static Map<String, Integer> toPart(String[] input) {
        var part = new HashMap<String, Integer>();
        part.put("x", Integer.parseInt(input[0].split("=")[1]));
        part.put("m", Integer.parseInt(input[1].split("=")[1]));
        part.put("a", Integer.parseInt(input[2].split("=")[1]));
        part.put("s", Integer.parseInt(input[3].split("=")[1]));
        return part;
    }

    private static Map<String, Rule> getSystem(Stream<String> split) {
        return split.map(s -> s.split("\\{")).collect(Collectors.toMap(s -> s[0], s -> toRule(s[1])));
    }

    private static Rule toRule(String input) {
        var split = Util.split(input.split("\\}")[0], ",").toList();
        return toSubRule(split);
    }

    private static Rule toSubRule(List<String> split) {
        if (split.size() > 0) {
            var s = split.get(0);
            Predicate<Map<String, Integer>> filter;
            String target;
            if (s.contains(":")) {
                var instruction = s.split(":");
                filter = getFilter(instruction[0]);
                target = instruction[1];
            } else {
                filter = map -> true;
                target = s;
            }
            return system -> (part -> {
                if (filter.test(part)) {
                    if (target.equals("A"))
                        return Result.A;
                    if (target.equals("R"))
                        return Result.R;
                    return system.get(target).apply(system).apply(part);
                }
                return toSubRule(split.subList(1, split.size())).apply(system).apply(part);
            });
        }
        throw new RuntimeException();
    }

    private static Predicate<Map<String, Integer>> getFilter(String filterString) {
        var value = Integer.parseInt(filterString.substring(2, filterString.length()));
        var test = filterString.substring(1, 2);
        BiPredicate<Integer, Integer> pred = test.equals("<") ? (a, b) -> a < b : (a, b) -> a > b;
        return part -> pred.test(part.get(filterString.substring(0, 1)), value);
    }
}
