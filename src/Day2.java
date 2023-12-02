import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.Math;

public class Day2 {

    private static final Map<String, Integer> RGB_INPUT = Map.of("red", 12, "green", 13, "blue", 14);

    public static void main(String[] args) { 
        calculate1("src/day2/example.txt");
        calculate1("src/day2/input.txt");
        calculate2("src/day2/example.txt");
        calculate2("src/day2/input.txt");
    }

    private static void calculate1(String filename){
        Util.applyAndPrint(filename, lines -> lines.map(line -> {
            var day = line.split(":");
            var games = day[1].split(";");
            var invalid = Stream.of(games).anyMatch(game -> !isValid(game));
            return invalid ? 0 : Integer.parseInt(day[0].split(" ")[1]);
        }).reduce(0, (a, b) -> a + b).toString()); 
    }

    private static boolean isValid(String game) {
        for(var color: game.split(",")){
            var colorSplit = color.strip().split(" ");
            if(Integer.parseInt(colorSplit[0]) > RGB_INPUT.get(colorSplit[1]))
                return false;
        }
        return true;
    }

    private static void calculate2(String filename){
        Util.applyAndPrint(filename, lines -> lines.map(line -> {
            var day = line.split(":");
            var games = day[1].split(";");
            var max = Stream.of(games)
                .map(game -> gameToMap(game))
                .reduce(Map.of("red", 0, "green", 0, "blue", 0), (current, next) -> getMaxOf(current, next));
            return max.values().stream().reduce(1, (a,b) -> a*b);
         }).reduce(0, (a, b) -> a + b).toString());         
    }

    private static Map<String, Integer> gameToMap(String game){
        return Stream.of(game.split(","))
            .map(color -> color.strip().split(" "))
            .collect(Collectors.toMap(split -> split[1], split -> Integer.parseInt(split[0])));
    }

    private static Map<String, Integer> getMaxOf(Map<String, Integer> currentMax, Map<String, Integer> next){
        return Stream.of("red", "green", "blue").collect(
            Collectors.toMap(
                color -> color, 
                color -> next.get(color) == null ? currentMax.get(color) : Math.max(currentMax.get(color), next.get(color))
            )
        );
    }
}
