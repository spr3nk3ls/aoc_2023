import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 {

  record Candidate(List<Character> springs, List<Integer> cons) {}

  public static void main(String[] args) {
    calculate("src/day12/example.txt", 1);
    calculate("src/day12/input.txt", 1);
    calculate("src/day12/example.txt", 5);
    calculate("src/day12/input.txt", 5); 
  }

  public static void calculate(String filename, int times){
    Util.applyAndPrint(filename, lines -> {
      Map<String, BigInteger> memo = new HashMap<>();
      var i = BigInteger.ZERO;
      for(var line : lines.toList()){
        i = i.add(getAllSolutions(line, times, memo));
      }
      return String.valueOf(i);
    }
    );
  }

  protected static BigInteger getAllSolutions(String line, int times, Map<String, BigInteger> memo){
    var split = line.strip().split(" ");
    var springsString = IntStream.range(0, times).mapToObj(i -> split[0]).collect(Collectors.joining("?"));
    var springs = springsString.chars().mapToObj(c -> (char)c).toList();
    var singleCons = Util.split(split[1], ",").map(Integer::parseInt).toList();
    var cons = IntStream.range(0, times).boxed().flatMap(i -> singleCons.stream()).toList();
    return getAllSolutions(new Candidate(springs, cons), memo);
  }

  protected static BigInteger getAllSolutions(Candidate mainCandidate, Map<String, BigInteger> memo){
    var hash = mainCandidate.toString();
    if(memo.containsKey(hash))
      return memo.get(hash);
    List<Candidate> possible = new ArrayList<>();
    possible.add(mainCandidate);
    List<Candidate> certain = new ArrayList<>();
    var k = BigInteger.ZERO;
    while(!possible.isEmpty()){
      for(int i = 0; i < possible.size(); i++){
        var candidate = possible.remove(i);
        var index = candidate.springs.indexOf('?');
        if(index == -1){
          int consSum = candidate.cons.stream().reduce(0, Integer::sum);
          if(candidate.springs.stream().filter(c -> c == '#').count() == consSum && isPossible(candidate)){
            certain.add(candidate);
            k = k.add(BigInteger.ONE);
          }
          break;
        }
        var with = new ArrayList<>(candidate.springs);
        with.set(index, '#');
        if(isPossible(candidate))
          possible.add(new Candidate(with, candidate.cons));
        var without = new ArrayList<>(candidate.springs);
        without.set(index, '.');
        if(isPossible(candidate))
          k = k.add(getAllSolutions(simplifiedCandidate(new Candidate(without, candidate.cons)), memo));
      }
    }
    memo.put(hash, k);
    return k;
  }

  private static Candidate simplifiedCandidate(Candidate candidate){
    var index = candidate.springs.stream()
        .map(String::valueOf)
        .collect(Collectors.joining()).indexOf(".?");
    if(index == -1)
      return candidate;
    var sub = candidate.springs.subList(0, index);
    var subString = sub.stream().map(Object::toString).collect(Collectors.joining());
    var lengths = Util.split(subString, "[.]+").map(String::length).filter(i -> i > 0).toList();
    var find = IntStream.range(0, candidate.cons.size())
      .filter(i -> candidate.cons.subList(0, i).equals(lengths))
      .findAny();
    if(find.isPresent()){
      return new Candidate(
        candidate.springs.subList(index, candidate.springs.size()), 
        candidate.cons.subList(find.getAsInt(), candidate.cons.size())
      );
    }
    return candidate;
  }

  private static boolean isPossible(Candidate candidate) {
    var end = candidate.springs.indexOf('?') == -1 ? candidate.springs.size() : candidate.springs.indexOf('?');
    var sub = candidate.springs.subList(0, end);
    var cons = candidate.cons;
    var subString = sub.stream().map(Object::toString).collect(Collectors.joining());
    var lengths = Util.split(subString, "[.]+").map(String::length).filter(i -> i > 0).toList();
    if(lengths.isEmpty())
      return true;
    if(cons.size() < lengths.size())
      return false;
    if(!lengths.subList(0, lengths.size() - 1).equals(cons.subList(0, lengths.size() - 1)))
      return false;
    return lengths.get(lengths.size() - 1) <= cons.get(lengths.size() - 1);
  }
}
