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

    static interface Module {
        String getName();

        String activate(String from, boolean fromPulse);

        List<String> pulse(Map<String, Module> moduleMap, Counter counter);

        List<String> getTargets();

        default List<String> innerPulse(Map<String, Module> moduleMap, Counter counter, boolean pulse) {
            var visited = new ArrayList<String>();
            for (var target : getTargets()) {
                if(getTargets().contains("rx") && pulse == false){
                    System.out.println(counter.tCount);
                    System.out.println(counter.fCount);
                    throw new RuntimeException();
                }
                if (pulse) {
                    counter.tCount = counter.tCount.add(BigInteger.ONE);
                } else {
                    counter.fCount = counter.fCount.add(BigInteger.ONE);
                }
                if (moduleMap.containsKey(target))
                    visited.add(moduleMap.get(target).activate(getName(), pulse));
            }
            return visited;
        }
    }

    static class FlipFlop implements Module {

        private final String name;
        private boolean on;
        private List<String> targets;

        FlipFlop(String name, List<String> targets) {
            this.name = name;
            this.targets = targets;
            on = false;
        }

        @Override
        public String toString() {
            return name + " " + targets.toString() + " flip " + on;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getTargets() {
            return targets;
        }

        @Override
        public String activate(String from, boolean fromPulse) {
            if (fromPulse == false) {
                on = !on;
                return name;
            }
            return null;
        }

        @Override
        public List<String> pulse(Map<String, Module> moduleMap, Counter counter) {
            return innerPulse(moduleMap, counter, on);
        }
    }

    static class Conjunction implements Module {

        private final String name;
        private Map<String, Boolean> sources = new HashMap<>();
        private List<String> targets;

        Conjunction(String name, List<String> targets) {
            this.name = name;
            this.targets = targets;
        }

        @Override
        public String toString() {
            return name + " " + targets.toString() + " sources " + sources;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<String> getTargets() {
            return targets;
        }

        public void addSource(String name) {
            sources.put(name, false);
        }

        @Override
        public String activate(String from, boolean fromPulse) {
            if (!sources.containsKey(from)) {
                throw new RuntimeException();
            }
            sources.put(from, fromPulse);
            return name;
        }

        @Override
        public List<String> pulse(Map<String, Module> moduleMap, Counter counter) {
            var pulse = sources.values().stream().allMatch(i -> i == true) ? false : true;
            return innerPulse(moduleMap, counter, pulse);
        }
    }

    static class Broadcaster implements Module {

        private List<String> targets;
        private boolean highPulse;

        Broadcaster(List<String> targets) {
            this.targets = targets;
        }

        @Override
        public String toString() {
            return "broadcaster " + targets.toString();
        }

        @Override
        public String getName() {
            return "broadcaster";
        }

        @Override
        public List<String> getTargets() {
            return targets;
        }

        @Override
        public String activate(String from, boolean fromPulse) {
            highPulse = fromPulse;
            return null;
        }

        @Override
        public List<String> pulse(Map<String, Module> moduleMap, Counter counter) {
            return innerPulse(moduleMap, counter, highPulse);
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
            Map<String, Module> moduleMap = toModuleMap(lines);
            setSources(moduleMap);
            var result = IntStream.range(0, 1000).mapToObj(i -> {
                var counter = cycle(moduleMap);
                return counter;
            }).reduce(new Counter(), (a, b) -> a.add(b));
            System.out.println(result.product());
        });
    }

    private static void calculate_b(String filename) {
        Util.applyToLines(filename, lines -> {
            Map<String, Module> moduleMap = toModuleMap(lines);
            setSources(moduleMap);
            var i = BigInteger.ZERO;
            while(true){
                i = i.add(BigInteger.ONE);
                try {
                    cycle(moduleMap);
                } catch (RuntimeException e){
                    System.out.println(i);
                }
            }
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
                moduleMap.get(name).pulse(moduleMap, counter).stream().filter(i -> i != null).forEach(queue::add);
        }
        return counter;
    }

    private static Map<String, Module> toModuleMap(Stream<String> lines) {
        return lines.map(line -> line.strip())
                .map(Day20::toModule)
                .collect(Collectors.toMap(m -> m.getName(), m -> m));
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
