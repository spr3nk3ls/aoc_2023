import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class Util {
    public static void applyAndPrint(String filename, Function<Stream<String>, String> function){
        try(var lines = Files.lines(Paths.get(filename))){
            System.out.println(function.apply(lines));
        } catch (IOException e){
            throw new RuntimeException();
        }
    }

    public static void applyToFile(String filename, Consumer<String> consumer){
        try {
            var content = Files.readString(Paths.get(filename));
            consumer.accept(content);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void applyToLines(String filename, Consumer<Stream<String>> consumer){
        try(var lines = Files.lines(Paths.get(filename))){
            consumer.accept(lines);
        } catch (IOException e){
            throw new RuntimeException();
        }
    }

    public static Stream<String> split(String string, String delimiter){
        return Stream.of(string.split(delimiter));
    }
}
