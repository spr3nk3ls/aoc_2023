import java.util.ArrayList;
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
                if(count == 0){
                    return 0;
                }
                return (int)Math.pow(2, count-1);
            }
        ).reduce(0, Integer::sum).toString());
    }

    private static void calculate2(String filename) {
        Util.applyAndPrint(filename, lines -> {
            var list = lines.map(Day4::getCard).toList();
            var llist = list.stream().map(l -> new ArrayList<>(List.of(l))).toList();
            IntStream.range(0, list.size()).forEach(i -> llist.get(i).forEach(card -> {
                var count = (int)card.winning.stream().filter(card.have::contains).count();
                IntStream.range(i+1, i+count+1).forEach(j -> llist.get(j).add(list.get(j)));
            }));
            return llist.stream().map(ArrayList::size).reduce(0, Integer::sum).toString();
        });
    }

    private static Card getCard(String line){
        var splitLine = line.split(":")[1].split("\\|");
        return new Card(
            Util.split(splitLine[0].strip(), "\\s+").map(Integer::parseInt).collect(Collectors.toList()),
            Util.split(splitLine[1].strip(), "\\s+").map(Integer::parseInt).collect(Collectors.toList())
        );
    }
}
