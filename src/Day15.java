import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Day15 {

    record Label(String label, Operation operation, Integer value){}

    enum Operation {PUT, TAKE}
    public static void main(String[] args) {
        calculate_a("src/day15/example.txt");
        calculate_a("src/day15/input.txt");
        calculate_b("src/day15/example.txt");
        calculate_b("src/day15/input.txt");
    }

    private static void calculate_a(String filename) {
        Util.applyAndPrint(filename, lines -> lines.findFirst().map(line -> {
            return Util.split(line.strip(), ",").map(sub -> getValue(sub)).reduce(0, (a, b) -> a+b).toString();
        }).orElse("")
        );
    }

    private static void calculate_b(String filename) {
        Util.applyAndPrint(filename, lines -> lines.findFirst().map(line -> {
            var labels = Util.split(line.strip(), ",").map(sub -> toLabel(sub)).iterator();
            LinkedHashMap<Integer, List<Label>> map = new LinkedHashMap<>(); 
            while(labels.hasNext()){
                operate(map, labels.next());
            }
            return collect(map).toString();
        }).orElse("")
        );
    }

    private static Label toLabel(String sub){
        if(sub.endsWith("-")){
            var label = sub.substring(0, sub.length() - 1);
            return new Label(label, Operation.TAKE, null);
        }
        var split = sub.split("=");
        return new Label(split[0], Operation.PUT, Integer.parseInt(split[1]));
    }

    private static void operate(Map<Integer, List<Label>> map, Label label){
        var value = getValue(label.label);
        if(!map.containsKey(value))
            map.put(value, new ArrayList<>());
        var list = map.get(value);
        if(label.operation == Operation.PUT){
            list.stream()
                .filter(ltr -> ltr.label.equals(label.label))
                .findAny().ifPresentOrElse(
                    ltr -> {
                        var index = list.indexOf(ltr);
                        list.set(index, label);
                    },
                () -> list.add(label));
        } else if (label.operation == Operation.TAKE){
            list.removeIf(ltr -> ltr.label.equals(label.label));
        }
    }

    private static Integer collect(LinkedHashMap<Integer, List<Day15.Label>> map) {
        var keysAsList = new ArrayList<Integer>();
        keysAsList.addAll(map.keySet());
        return IntStream.range(0, map.size())
            .filter(box -> map.get(keysAsList.get(box)) != null)
            .map(box -> {
                var boxContent = map.get(keysAsList.get(box));
                return IntStream.range(0, boxContent.size()).map(lens -> {
                    return (keysAsList.get(box) + 1)*(lens + 1)*boxContent.get(lens).value;
            }).sum();
        }).sum();
    }

    private static Integer getValue(String sub){
        var chars = sub.chars().mapToObj(i -> (int)i).iterator();
        var current = 0;
        while(chars.hasNext()){
            var c = chars.next();
            current += c;
            current *= 17;
            current = current % 256;
        }
        return current;
    }
    
}
