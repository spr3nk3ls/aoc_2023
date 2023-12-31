import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Day6 {

    private static final Map<Character, Integer> suits = Map.of('A', 14, 'K', 13, 'Q', 12, 'T', 10);

    final record Hand(List<Integer> cards, Integer bid, Integer jokers) {}

    public static void main(String[] args) {
        calculate("src/day6/example.txt", Day6::toHandNoJokers);
        calculate("src/day6/input.txt", Day6::toHandNoJokers);
        calculate("src/day6/example.txt", Day6::toHandWithJokers);
        calculate("src/day6/input.txt", Day6::toHandWithJokers);
    }

    private static void calculate(String filename, Function<String, Hand> toHand) {
        Util.applyAndPrint(filename, lines -> {
            var cards = lines.map(toHand).sorted(COMP).toList();
            return String.valueOf(IntStream.range(0, cards.size())
                .map(i -> (i+1)*cards.get(i).bid)
                .sum());
        });
    }

    private static Hand toHandNoJokers(String line){
        return toHand(line, 11, 0);
    }

    private static Hand toHandWithJokers(String line){
        return toHand(line, 1, (int)line.split("\\s+")[0].chars().filter(ch -> ch == 'J').count());
    }

    private static Hand toHand(String line, Integer jValue, Integer numberOfJokers){
        var split = line.split("\\s+");
        var cards = split[0].chars().mapToObj(c -> (char)c).map(c -> {
            return c == 'J' ? jValue : (suits.containsKey(c) ? suits.get(c) : Character.getNumericValue(c));        
        }).toList();
        return new Hand(cards, Integer.parseInt(split[1]), numberOfJokers);
    }

    private static final Comparator<Hand> COMP = new Comparator<Hand>(){

        final record Group(Integer value, Integer count) {}

        @Override
        public int compare(Hand hand1, Hand hand2) {
            var groups1 = getGroups(hand1.cards);
            var groups2 = getGroups(hand2.cards);
            var hand1WithJokers = groups1.get(0).count + hand1.jokers;
            var hand2WithJokers = groups2.get(0).count + hand2.jokers;
            if(hand1WithJokers != hand2WithJokers)
                return ((Integer)hand1WithJokers).compareTo(hand2WithJokers);
            if(groups1.get(1).count != groups2.get(1).count)
                return groups1.get(1).count.compareTo(groups2.get(1).count);
            return IntStream.range(0, 5)
                .filter(i -> hand1.cards.get(i) != hand2.cards.get(i))
                .map(i -> hand1.cards.get(i).compareTo(hand2.cards.get(i)))
                .findFirst().orElse(0);
        }

        private static final List<Group> getGroups(List<Integer> cards) {
            return IntStream.range(2, 15)
                .mapToObj(i -> new Group(i, Collections.frequency(cards, i)))
                .sorted(new Comparator<Group>() {
                    @Override
                    public int compare(Group o1, Group o2) {
                        return o2.count.compareTo(o1.count);
                    }
                }).toList();
        }
    };
}