import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 {

  public static void main(String[] args) {
    calculate("src/day12/example.txt", 1);
    calculate("src/day12/input.txt", 1);
    // calculate("src/day12/example.txt", 5);
    // calculate("src/day12/input.txt", 5);
  }

  public static void calculate(String filename, int times){
    Util.applyAndPrint(filename, lines ->
      lines.map(line -> getAllSolutions(line, times))
          .map(List::size)
          .reduce(0, Integer::sum).toString()
    );
  }

  protected static List<String> getAllSolutions(String line, int times){
    var split = line.strip().split(" ");
    var springsString = IntStream.range(0, times).mapToObj(i -> split[0]).collect(Collectors.joining("?"));
    var springs = springsString.chars().mapToObj(c -> (char)c).toList();
    var singleCons = Util.split(split[1], ",").map(Integer::parseInt).toList();
    var cons = IntStream.range(0, times).boxed().flatMap(i -> singleCons.stream()).toList();
    int consSum = cons.stream().reduce(0, Integer::sum);
    List<List<Character>> possible = new ArrayList<>();
    possible.add(springs);
    List<List<Character>> certain = new ArrayList<>();
    while(!possible.isEmpty()){
      for(int i = 0; i < possible.size(); i++){
        var candidate = possible.remove(i);
        var index = candidate.indexOf('?');
        if(index == -1){
          if(candidate.stream().filter(c -> c == '#').count() == consSum && isPossibleSub(candidate, cons))
            certain.add(candidate);
          break;
        }
        var with = new ArrayList<>(candidate);
        with.set(index, '#');
        if(isPossible(candidate, cons, consSum))
          possible.add(with);
        var without = new ArrayList<>(candidate);
        without.set(index, '.');
        if(isPossible(candidate, cons, consSum))
          possible.add(without);
      }
    }
    return certain.stream().map(Object::toString).toList();
  }

  private static boolean isPossible(List<Character> full, List<Integer> cons, Integer totalHashes) {
    var hashes = full.stream().filter(c -> c == '#').count();
    var qMarks = full.stream().filter(c -> c == '?').count();
    if(totalHashes < hashes || totalHashes > hashes + qMarks)
      return false;
    var dots = full.stream().filter(c -> c == '.').count();
    var totalDots = full.size() - totalHashes;
    if(totalDots < dots || totalDots > dots + qMarks)
      return false;
    var sub = full.subList(0, full.indexOf('?'));
    return isPossibleSub(sub, cons);
  }

  private static boolean isPossibleSub(List<Character> sub, List<Integer> cons) {
    var subString = sub.stream().map(Object::toString).collect(Collectors.joining());
    var lengths = Util.split(subString, "[.]+").map(String::length).filter(i -> i > 0).toList();
    if(lengths.isEmpty())
      return true;
    if(cons.size() < lengths.size())
      return false;
    if(!lengths.subList(0, lengths.size() - 1).equals(cons.subList(0, lengths.size() - 1))){
      return false;
    }
    return lengths.get(lengths.size() - 1) <= cons.get(lengths.size() - 1);
  }
}
