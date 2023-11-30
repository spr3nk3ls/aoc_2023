import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Util.applyToLines("src/hai.txt", lines -> lines.forEach(System.out::println));
        Util.applyToFile("src/hai2.txt", content -> Stream.of(content.split("\n\n")).map(lines -> lines + "\nsplit").forEach(System.out::println));
    }
}