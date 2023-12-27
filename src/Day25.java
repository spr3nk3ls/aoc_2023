import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class Day25 {
    public static void main(String[] args) {
        var pairs_example = List.of(List.of("cmg", "bvb"), List.of("nvd", "jqt"), List.of("pzl", "hfx"));
        calculate("src/day25/example.txt", pairs_example);
        var pairs_input = List.of(List.of("tmt", "pnz"), List.of("gbc", "hxr"), List.of("mvv", "xkz"));
        calculate("src/day25/input.txt", pairs_input);
    }

    private static void calculate(String filename, List<List<String>> pairs) {
        Util.applyAndPrint(filename, fileLines -> {
            Map<String, List<String>> map = toMap(fileLines); 
            var count1 = countNodes(map, pairs.get(0).get(0));
            print(map);
            // $ neato -Tsvg output.dot > output.svg
            map = disconnect(map, pairs);
            var count2 = countNodes(map, pairs.get(0).get(0));
            return String.valueOf(count2*(count1 - count2));
        });
    }

    private static Map<String, List<String>> toMap(Stream<String> fileLines) {
        var it = fileLines.iterator();
        var map = new HashMap<String, List<String>>();
        while (it.hasNext()) {
            var line = it.next();
            var split = line.split(": ");
            map.put(split[0], new ArrayList<>());
            Util.split(split[1], " ")
                    .forEach(v -> map.get(split[0]).add(v));
        }
        return map;
    }

    private static void print(Map<String, List<String>> map){
        try(var w = new FileWriter("src/day25/output.dot")){
            w.write("graph G {\n");
            for(var key : map.keySet()){
                for(var value : map.get(key)){
                    w.write("    " + key + " -- " + value + ";\n");
                }
            }
            w.write("}");
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private static Map<String, List<String>> disconnect(Map<String, List<String>> map, List<List<String>> connections){
        for(var c : connections){
            if(map.containsKey(c.get(0)) && map.get(c.get(0)).contains(c.get(1))){
                map.get(c.get(0)).remove(c.get(1));
            } else if(map.containsKey(c.get(1)) && map.get(c.get(1)).contains(c.get(0))){
                map.get(c.get(1)).remove(c.get(0));
            } else {
                System.out.println(c);
                System.out.println(map);
                throw new RuntimeException();
            }
        }
        return map;
    }

    private static int countNodes(Map<String, List<String>> map, String start){
        var realStart = start;
        if(!map.containsKey(start)){
            realStart = map.entrySet().stream().filter(e -> e.getValue().contains(start)).map(e -> e.getKey()).findFirst().get();
        }
        var q = new PriorityQueue<String>();
        q.add(realStart);
        var result = new HashSet<String>();
        result.add(realStart);
        while(!q.isEmpty()){
            var top = q.poll();
            if(map.containsKey(top)){
                var pointingValues = map.get(top);
                var pointingValuesNotInResult = pointingValues.stream().filter(v -> !result.contains(v)).toList();
                q.addAll(pointingValuesNotInResult);

                var pointingKeys = map.get(top).stream()
                .flatMap(v -> map.entrySet().stream().filter(e -> e.getValue().contains(v)).map(e -> e.getKey())).toList();
                var pointingKeysNotInResult = pointingKeys.stream().filter(v -> !result.contains(v)).toList();
                q.addAll(pointingKeysNotInResult);

                result.addAll(map.get(top));
                result.addAll(pointingKeys);
            }
        }
        return result.size();
    }

}