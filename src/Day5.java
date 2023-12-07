import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day5 {

    record Mapping(BigInteger targetStart, BigInteger sourceStart, BigInteger range){}

    public static void main(String[] args) {
        calculate1("src/day5/example.txt");
        calculate1("src/day5/input.txt");
        calculate2("src/day5/example.txt");
        calculate2("src/day5/input.txt");
    }

    private static void calculate1(String filename) {
        Util.applyToFile(filename, file -> {
            var splitFile = file.split("\n\n");
            var seeds = Stream.of(splitFile[0].split(":")[1].strip().split("\\s+")).map(i -> new BigInteger(i));
            System.out.println(getFinalSeeds(seeds, splitFile).min(Comparator.naturalOrder()).orElseThrow());
        });
    }

    private static void calculate2(String filename) {
        Util.applyToFile(filename, file -> {
            var splitFile = file.split("\n\n");
            var numInput = Stream.of(splitFile[0].split(":")[1].strip().split("\\s+")).map(i -> new BigInteger(i)).toList();
            var seeds = Stream.concat(
                Stream.iterate(numInput.get(0), i -> i.add(BigInteger.ONE)).limit(numInput.get(1).longValue()),
                Stream.iterate(numInput.get(2), i -> i.add(BigInteger.ONE)).limit(numInput.get(3).longValue())
            ).parallel();
            System.out.println(getFinalSeeds2(seeds, splitFile).min(Comparator.naturalOrder()).orElseThrow());
        });
    }

    private static Stream<BigInteger> getFinalSeeds(Stream<BigInteger> seeds, String[] splitFile){
        var mappings = IntStream.range(1, splitFile.length).mapToObj(i -> getMappings(splitFile[i]).toList());
        for(var mapping : mappings.toList()){
            seeds = seeds.map(s -> transform(s, mapping));
        }
        return seeds;
    }

    private static Stream<BigInteger> getFinalSeeds2(Stream<BigInteger> seeds, String[] splitFile){
        var mappings = IntStream.range(1, splitFile.length).mapToObj(i -> getMappings(splitFile[i]).toList());
        return mappings.reduce(seeds, (seed, m) -> seed.map(s -> transform(s, m)), (s1, s2) -> s2);
    }

    private static Stream<Mapping> getMappings(String mappingString){
        var split = mappingString.split("\n");
        return IntStream.range(1, split.length).mapToObj(i -> {
            var numbers = Util.split(split[i], " ").map(j -> new BigInteger(j)).toList();
            return new Mapping(numbers.get(0), numbers.get(1), numbers.get(2));
        }).parallel();
    }

    private static BigInteger transform(BigInteger source, List<Mapping> mappings){
        return mappings.parallelStream()
            .filter(m -> (source.compareTo(m.sourceStart) >= 0 && source.compareTo(m.sourceStart.add(m.range)) <= 0))
            .map(m -> source.add(m.targetStart).subtract(m.sourceStart))
            .findAny().orElse(source);
    }
}