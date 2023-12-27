import java.awt.geom.Point2D;
import java.util.stream.Stream;

public class Day24 {

    record Hail(Point2D origin, Vector dir) {
    }

    record Vector(double px, double py) {
    }

    public static void main(String[] args) {
        calculate("src/day24/example.txt", 7L, 27L);
        calculate("src/day24/input.txt", 200000000000000L, 400000000000000L);
    }

    private static void calculate(String filename, long min, long max) {
        Util.applyAndPrint(filename, fileLines -> {
            var hails = toHails(fileLines).toList();
            int i = 0;
            for (var hail1 : hails) {
                for (var hail2 : hails) {
                    if (!hail1.equals(hail2)) {
                        var sect = intersection(hail1, hail2);
                        if (sect != null && sect.getX() > min && sect.getX() < max && sect.getY() > min
                                && sect.getY() < max){
                            i++;
                        }
                    }
                }
            }
            return String.valueOf(i / 2);
        });
    }

    private static Stream<Hail> toHails(Stream<String> fileLines) {
        return fileLines.map(fl -> {
            var split = fl.split(" @ ");
            var start = Util.split(split[0], ", ").map(s -> s.strip()).map(Double::parseDouble).toList();
            var startPoint = new Point2D.Double(start.get(0), start.get(1));
            var dir = Util.split(split[1], ", ").map(s -> s.strip()).map(Double::parseDouble).toList();
            return new Hail(startPoint, new Vector(dir.get(0), dir.get(1)));
        });
    }

    public static Point2D intersection(Hail a, Hail b) {
        var dx = b.origin.getX() - a.origin.getX();
        var dy = b.origin.getY() - a.origin.getY();
        var det = b.dir.px * a.dir.py - b.dir.py * a.dir.px;
        if (Math.abs(det) < 0.00001)
            return null;
        var u = (dy * b.dir.px - dx * b.dir.py) / det;
        var v = (dy * a.dir.px - dx * a.dir.py) / det;
        if (u < 0 || v < 0) {
            return null;
        }
        return new Point2D.Double(a.origin.getX() + u * a.dir.px, a.origin.getY() + u * a.dir.py);
    }
}