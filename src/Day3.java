import java.util.stream.Collectors;

public class Day3 {

    public static void main(String[] args) {
        calculate1("src/day1/example1.txt"); //4361
        calculate1("src/day1/input1.txt"); 
        // calculate2("src/day1/example2.txt");
        // calculate2("src/day1/input1.txt");
    }

    private static void calculate1(String filename) {
        Util.applyAndPrint(filename, lines -> lines.collect(Collectors.joining()));
    }
}
