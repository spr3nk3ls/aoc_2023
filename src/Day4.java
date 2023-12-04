import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day4 {

    record Card(List<Integer> winning, List<Integer> have) {}

    public static void main(String[] args) {
        calculate1("src/day4/example.txt");
        calculate1("src/day4/input.txt");
        calculate2("src/day4/example.txt");
        calculate2("src/day4/input.txt");
    }

    private static void calculate1(String filename) {
        Util.applyAndPrint(filename, lines -> lines.map(Day4::getCard).map(card -> {
                long count = card.winning.stream().filter(card.have::contains).count();
                return count == 0 ? 0 : (int)Math.pow(2, count-1);
            }
        ).reduce(0, Integer::sum).toString());
    }
    private static void calculate2(String filename) {
        Util.applyAndPrint(filename, lines -> {
            var cards = lines.map(Day4::getCard)
                .map(card -> (int)card.winning.stream().filter(card.have::contains).count())
                .toList();
            var won = IntStream.generate(() -> 1).limit(cards.size()).toArray();
            IntStream.range(0, cards.size()).forEach(i ->
                IntStream.range(i + 1, i + 1 + cards.get(i)).forEach(j -> won[j] += won[i])
            );
            return String.valueOf(Arrays.stream(won).reduce(0, Integer::sum));
        });
    }

    private static Card getCard(String line){
        var splitLine = line.split(":")[1].split("\\|");
        return new Card(stripToInt(splitLine[0]), stripToInt(splitLine[1]));
    }

    private static List<Integer> stripToInt(String input){
        return Util.split(input.strip(), "\\s+").map(Integer::parseInt).collect(Collectors.toList());
    }
}
