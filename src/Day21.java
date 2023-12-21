import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day21 {

    record Coordinates(int x, int y) {
        public Coordinates add(Coordinates direction) {
            return new Coordinates(this.x + direction.x, this.y + direction.y);
        }
    }

    enum Dir {
        U(new Coordinates(0, -1)),
        D(new Coordinates(0, 1)),
        R(new Coordinates(1, 0)),
        L(new Coordinates(-1, 0));

        private final Coordinates coordinates;

        Dir(Coordinates coordinates) {
          this.coordinates = coordinates;
        }

        Coordinates c(){
            return coordinates;
        }
    }

    public static void main(String[] args) {
        calculate("src/day21/example.txt", 6);
        calculate("src/day21/input.txt", 64);
        calculate("src/day21/input.txt", 26501365);
    }

    private static void calculate(String filename, int steps) {
        Util.applyToFile(filename, asString -> {
            var matrix = Util.split(asString, "\n")
                .map(String::toCharArray)
                .toArray(char[][]::new);
            var start = start(matrix);
            Set<Coordinates> circle = new HashSet<>(Set.of(start));
            Set<Coordinates> full = new HashSet<>(Set.of(start));
            for(int i = 0; i < steps/2; i++){
                var dirtyCircle = twoSteps(circle, matrix);
                circle = dirtyCircle.stream().filter(c -> !full.contains(c)).collect(Collectors.toSet());
                full.addAll(circle);
            }
            System.out.println(full.size());
        });
    }

    private static Coordinates start(char[][] matrix){
        int start_y = IntStream.range(0, matrix.length).filter(y -> new String(matrix[y]).contains("S")).findAny().getAsInt();
        int start_x = IntStream.range(0, matrix[start_y].length).filter(x -> matrix[start_y][x] == 'S').findAny().getAsInt();
        return new Coordinates(start_x, start_y);
    }

    private static Set<Coordinates> twoSteps(Set<Coordinates> oldCircle, char[][] matrix){
        var newDirtyCircle = new HashSet<Coordinates>();
        for(var c : oldCircle){
            newDirtyCircle.addAll(twoStepsFromPoint(c, matrix));
        }
        return newDirtyCircle;
    }

    private static Set<Coordinates> twoStepsFromPoint(Coordinates c, char[][] m){
        var set = new HashSet<Coordinates>();
        var twoUp = c.add(Dir.U.c()).add(Dir.U.c());
        if(free(c.add(Dir.U.c()), m) && free(twoUp, m))
            set.add(twoUp);
        var twoDown = c.add(Dir.D.c()).add(Dir.D.c());
        if(free(c.add(Dir.D.c()), m) && free(twoDown, m))
            set.add(twoDown);
        var twoLeft = c.add(Dir.L.c()).add(Dir.L.c());
        if(free(c.add(Dir.L.c()), m) && free(twoLeft, m))
            set.add(twoLeft);
        var twoRight = c.add(Dir.R.c()).add(Dir.R.c());
        if(free(c.add(Dir.R.c()), m) && free(twoRight, m))
            set.add(twoRight);
        var upLeft = c.add(Dir.U.c()).add(Dir.L.c());
        if((free(c.add(Dir.U.c()), m) || free(c.add(Dir.L.c()), m)) && free(upLeft, m))
            set.add(upLeft);
        var upRight = c.add(Dir.U.c()).add(Dir.R.c());
        if((free(c.add(Dir.U.c()), m) || free(c.add(Dir.R.c()), m)) && free(upRight, m))
            set.add(upRight);
        var downLeft = c.add(Dir.D.c()).add(Dir.L.c());
        if((free(c.add(Dir.D.c()), m) || free(c.add(Dir.L.c()), m)) && free(downLeft, m))
            set.add(downLeft);
        var downRight = c.add(Dir.D.c()).add(Dir.R.c());
        if((free(c.add(Dir.D.c()), m) || free(c.add(Dir.R.c()), m)) && free(downRight, m))
            set.add(downRight);
        return set;
    }

    private static boolean free(Coordinates coordinate, char[][] matrix){
        if(coordinate.y < 0 || coordinate.y >= matrix.length || 
            coordinate.x < 0 || coordinate.x >= matrix[0].length ||
            matrix[coordinate.y][coordinate.x] == '#')
            return false;
        return true;
    }
    
}
