import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day19b {

    enum Result {
        A, R
    }

    record Test(String key, Predicate<Integer> test) {
    }

    static class Either {
        private Day19b.Result result;
        private Day19b.Node node;

        Either(Result result) {
            this.result = result;
        }

        Either(Node node) {
            this.node = node;
        }
    }

    record Node(String test, Either t, Either f) {
    }

    public static void main(String[] args) {
        calculate_b("src/day19/example.txt");
        calculate_b("src/day19/input.txt");
    }

    private static void calculate_b(String filename) {
        Util.applyToFile(filename, lines -> {
            var split = lines.split("\n\n");
            var system = getSystem(Util.split(split[0].strip(), "\n"));
            var start = toNode(system.get("in"), system);
            var backtrackList = new ArrayList<List<Test>>();
            backtrack(start.node, backtrackList, new ArrayList<Test>());
            var result = backtrackList.stream().map(track -> {
                return Stream.of("x", "m", "a", "s").map(key -> {
                    var predicates = track.stream().filter(test -> test.key.equals(key)).map(test -> test.test)
                            .toList();
                    var count = (int) IntStream.range(1, 4001).filter(x -> predicates.stream().allMatch(p -> p.test(x)))
                            .count();
                    return BigInteger.valueOf(count);
                }).reduce(BigInteger.ONE, (a, b) -> a.multiply(b));
            }).reduce(BigInteger.ZERO, (a, b) -> a.add(b));
            System.out.println(result);
        });
    }

    private static Map<String, String> getSystem(Stream<String> split) {
        return split.map(s -> s.strip().split("\\{"))
                .collect(Collectors.toMap(
                        s -> s[0],
                        s -> s[1].substring(0, s[1].length() - 1)));
    }

    private static Either toNode(String nodeValue, Map<String, String> system) {
        var testEnd = nodeValue.indexOf(":");
        if (testEnd == -1) {
            // Node is primitive
            switch (nodeValue) {
                case "R":
                    return new Either(Result.R);
                case "A":
                    return new Either(Result.A);
                default:
                    return toNode(system.get(nodeValue), system);
            }
        }
        // Node is complex
        var test = nodeValue.substring(0, testEnd);
        var trueEnd = nodeValue.indexOf(",");
        var trueValue = toNode(nodeValue.substring(testEnd + 1, trueEnd), system);
        var falseValue = toNode(nodeValue.substring(trueEnd + 1, nodeValue.length()), system);
        return new Either(new Node(test, trueValue, falseValue));
    }

    private static void backtrack(Node node, List<List<Test>> fullList, List<Test> currentTrack) {
        if (node.t.result == Result.A) {
            var newTrack = new ArrayList<>(currentTrack);
            newTrack.add(toTest(node.test, true));
            fullList.add(newTrack);
        }
        if (node.f.result == Result.A) {
            var newTrack = new ArrayList<>(currentTrack);
            newTrack.add(toTest(node.test, false));
            fullList.add(newTrack);
        }
        if (node.t.node != null) {
            var newTrack = new ArrayList<>(currentTrack);
            newTrack.add(toTest(node.test, true));
            backtrack(node.t.node, fullList, newTrack);
        }
        if (node.f.node != null) {
            var newTrack = new ArrayList<>(currentTrack);
            newTrack.add(toTest(node.test, false));
            backtrack(node.f.node, fullList, newTrack);
        }
    }

    private static Test toTest(String test, boolean b) {
        var key = test.substring(0, 1);
        var predicate = toPredicate(test.substring(1, test.length()), b);
        return new Test(key, predicate);
    }

    private static Predicate<Integer> toPredicate(String substring, boolean b) {
        var value = Integer.parseInt(substring.substring(1, substring.length()));
        return substring.substring(0, 1).equals(">") ? a -> a > value == b : a -> a < value == b;
    }
}
