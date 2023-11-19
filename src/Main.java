public class Main {
    public static void main(String[] args) {
        Util.applyToFile("src/hai.txt", lines -> lines.forEach(System.out::println));
    }
}