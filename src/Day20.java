import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day20 {

    static class Counter {
        BigInteger tCount = BigInteger.ZERO;
        BigInteger fCount = BigInteger.ZERO;

        Counter add(Counter other) {
            this.tCount = this.tCount.add(other.tCount);
            this.fCount = this.fCount.add(other.fCount);
            return this;
        }

        BigInteger product() {
            return this.tCount.multiply(this.fCount);
        }
    }

    static abstract class Module {
        final String name;
        final List<String> targets;

        Module(String name, List<String> targets) {
            this.name = name;
            this.targets = targets;
        }

        public String getName() {
            return name;
        }

        public List<String> getTargets() {
            return targets;
        }

        List<String> pulse(Map<String, Module> moduleMap, Counter counter) {
            var visited = new ArrayList<String>();
            for (var target : getTargets()) {
                if (getPulse()) {
                    counter.tCount = counter.tCount.add(BigInteger.ONE);
                } else {
                    counter.fCount = counter.fCount.add(BigInteger.ONE);
                }
                if (moduleMap.containsKey(target) && moduleMap.get(target).activate(getName(), getPulse()))
                    visited.add(target);
            }
            return visited;
        }

        abstract boolean activate(String from, boolean fromPulse);

        abstract boolean getPulse();
    }

    static class FlipFlop extends Module {

        private boolean on;

        FlipFlop(String name, List<String> targets) {
            super(name, targets);
            on = false;
        }

        @Override
        public boolean activate(String from, boolean fromPulse) {
            if (fromPulse == false) {
                on = !on;
                return true;
            }
            return false;
        }

        @Override
        boolean getPulse() {
            return on;
        }
    }

    static class Conjunction extends Module {

        private Map<String, Boolean> sources = new HashMap<>();

        Conjunction(String name, List<String> targets) {
            super(name, targets);
        }

        @Override
        public boolean activate(String from, boolean fromPulse) {
            sources.put(from, fromPulse);
            return true;
        }

        public void addSource(String name) {
            sources.put(name, false);
        }

        @Override
        boolean getPulse() {
            return !sources.values().stream().allMatch(v -> v == true);
        }
    }

    static class Broadcaster extends Module {

        private boolean highPulse;

        Broadcaster(List<String> targets) {
            super("broadcaster", targets);
        }

        @Override
        public boolean activate(String from, boolean fromPulse) {
            highPulse = fromPulse;
            return false;
        }

        @Override
        boolean getPulse() {
            return highPulse;
        }
    }

    public static void main(String[] args) {
        calculate_a("src/day20/example1.txt");
        calculate_a("src/day20/example2.txt");
        calculate_a("src/day20/input.txt");
        calculate_b("src/day20/input.txt");
    }

    private static void calculate_a(String filename) {
        Util.applyToLines(filename, lines -> {
            var moduleMap = toModuleMap(lines);
            var result = IntStream.range(0, 1000).mapToObj(j -> {
                var counter = cycle(moduleMap);
                return counter;
            }).reduce(new Counter(), (a, b) -> a.add(b));
            System.out.println(result.product());
        });
    }

    private static void calculate_b(String filename) {
        Util.applyToLines(filename, lines -> {
            var product = BigInteger.ONE;
            var conjunctions = List.of("vv", "nt", "vn", "zq"); // Conjunctions three steps from final module
            var lineList = lines.toList();
            for (var conjunction : conjunctions) {
                var moduleMap = toModuleMap(lineList.stream());
                var j = 0;
                while (true) {
                    j++;
                    var part = cycle_i(moduleMap, j, conjunction);
                    if (part != 0) {
                        product = product.multiply(BigInteger.valueOf(part));
                        break;
                    }
                }
            }
            System.out.println(product);
        });
    }

    private static Counter cycle(Map<String, Module> moduleMap) {
        var counter = new Counter();
        counter.fCount = counter.fCount.add(BigInteger.ONE);
        moduleMap.get("broadcaster").activate(null, false);
        var queue = new PriorityQueue<String>();
        queue.add("broadcaster");
        while (!queue.isEmpty()) {
            var name = queue.remove();
            if (moduleMap.containsKey(name))
                moduleMap.get(name).pulse(moduleMap, counter).stream().filter(j -> j != null).forEach(queue::add);
        }
        return counter;
    }

    private static int cycle_i(Map<String, Module> moduleMap, int i, String conjunction) {
        moduleMap.get("broadcaster").activate(null, false);
        var queue = new PriorityQueue<String>();
        queue.add("broadcaster");
        while (!queue.isEmpty()) {
            var name = queue.remove();
            if (moduleMap.containsKey(name))
                moduleMap.get(name).pulse(moduleMap, new Counter()).stream().filter(j -> j != null).forEach(queue::add);
            if (!((Conjunction) moduleMap.get(conjunction)).getPulse()) {
                return i;
            }
        }
        return 0;
    }

    private static Map<String, Module> toModuleMap(Stream<String> lines) {
        var map = lines.map(line -> line.strip())
                .map(Day20::toModule)
                .collect(Collectors.toMap(m -> m.getName(), m -> m));
        setSources(map);
        return map;
    }

    private static Module toModule(String line) {
        var split = line.split(" -> ");
        var source = split[0];
        var targets = List.of(split[1].split(", "));
        switch (source.substring(0, 1)) {
            case "b":
                return new Broadcaster(targets);
            case "%":
                return new FlipFlop(source.substring(1, source.length()), targets);
            case "&":
                return new Conjunction(source.substring(1, source.length()), targets);
            default:
                throw new RuntimeException();
        }
    }

    private static void setSources(Map<String, Module> moduleMap) {
        for (var module : moduleMap.values()) {
            var targets = module.getTargets();
            for (var target : targets) {
                var conj = moduleMap.get(target);
                if (conj instanceof Conjunction) {
                    ((Conjunction) conj).addSource(module.getName());
                }
            }
        }
    }
}
