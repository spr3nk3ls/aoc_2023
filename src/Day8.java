import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day8 {

    static final class LR {

        private final String left;
        private final String right;

        public LR(String left, String right) {
            this.left = left;
            this.right = right;
        }

        public String get(Character lOrR){
            if(lOrR.equals('L'))
                return left;
            if(lOrR.equals('R'))
                return right;
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        calculate1("src/day8/example1.txt");
        calculate1("src/day8/input.txt");
        calculate2("src/day8/example2.txt");
        calculate2("src/day8/input.txt");
    }

    private static void calculate1(String filename) {
        Util.applyToFile(filename, lines -> {
            var split = lines.split("\n\n");
            var instructions = split[0].strip().chars().mapToObj(c -> (char)c).toList();
            var size = instructions.size();
            var nodes = getNodeMap(split[1]);
            var i = 0;
            String node = "AAA";
            while(true){
                var c = instructions.get(i % size);
                node = nodes.get(node).get(c);
                if(node.equals("ZZZ")){
                    System.out.println(i+1);
                    return;
                }
                i++;
            }
        });
    }

    private static void calculate2(String filename) {
        Util.applyToFile(filename, lines -> {
            var split = lines.split("\n\n");
            var instructions = split[0].strip().chars().mapToObj(c -> (char)c).toList();
            var size = instructions.size();
            var nodes = getNodeMap(split[1]);
            var node = nodes.keySet().stream().filter(s -> s.endsWith("A")).collect(Collectors.toList());
            List<Integer> tails = new ArrayList<>();
            List<Integer> cycleLengths = new ArrayList<>();
            List<Integer> zPos = new ArrayList<>();
            for(var n : node){
                var i = 0;
                var cycle = new ArrayList<String>();
                var next = n;
                inner: while(true){
                    var c = instructions.get(i % size);
                    next = nodes.get(next).get(c);
                    if(cycle.contains(next) && cycle.indexOf(next) - i % size == 0){
                        tails.add(cycle.indexOf(next));
                        cycleLengths.add(i - cycle.indexOf(next));
                        break inner;
                    }
                    cycle.add(next);
                    if(next.endsWith("Z"))
                        zPos.add(cycle.indexOf(next));
                    i++;
                }
            }
            System.out.println(cycleLengths); //Gebruik wolfram alpha om lcm te berekenen, Java is moeilijk.
        });
    }

    private static Map<String,LR> getNodeMap(String string) {
        return Util.split(string,"\n").map(s -> s.split("="))
            .collect(Collectors.toMap(s -> s[0].strip(), s -> {
                var s2 = s[1].strip();
                return new LR(s2.substring(1,4), s2.substring(6,9));
            }));
    }
}
