import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

public class Day24 {

    record Hail(Point origin, Vector dir) {
        Hail speedUp(Vector otherVector) {
            return new Hail(origin, new Vector(this.dir.px + otherVector.px, this.dir.py + otherVector.py, this.dir.pz + otherVector.pz));
        }
    }

    record Point(double x, double y, double z){}

    record Vector(double px, double py, double pz) {
    }

    public static void main(String[] args) {
        calculate("src/day24/example.txt", 7L, 27L);
        calculate("src/day24/input.txt", 200000000000000L, 400000000000000L);
        calculate("src/day24/example.txt", 5);
        calculate("src/day24/input.txt", 200);
    }

    private static void calculate(String filename, long min, long max) {
        Util.applyAndPrint(filename, fileLines -> {
            var hails = toHails(fileLines).toList();
            int i = 0;
            for (var hail1 : hails) {
                for (var hail2 : hails) {
                    if (!hail1.equals(hail2)) {
                        var sect = intersection(hail1, hail2);
                        if (sect != null && sect.x > min && sect.x < max && sect.y > min
                                && sect.y < max) {
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
            var startPoint = new Point(start.get(0), start.get(1), start.get(2));
            var dir = Util.split(split[1], ", ").map(s -> s.strip()).map(Double::parseDouble).toList();
            return new Hail(startPoint, new Vector(dir.get(0), dir.get(1), dir.get(2)));
        });
    }

    public static Point intersection(Hail a, Hail b) {
        var dx = b.origin.x - a.origin.x;
        var dy = b.origin.y - a.origin.y;
        var det = b.dir.px * a.dir.py - b.dir.py * a.dir.px;
        if (Math.abs(det) < 0.00001)
            return null;
        var u = (dy * b.dir.px - dx * b.dir.py) / det;
        var v = (dy * a.dir.px - dx * a.dir.py) / det;
        if (u < -0.00001 || v < -0.00001) {
            return null;
        }
        return new Point(a.origin.x + u * a.dir.px, a.origin.y + u * a.dir.py, 0);
    }

    private static void calculate(String filename, int area) {
        Util.applyAndPrint(filename, fileLines -> {
            var hails = toHails(fileLines).toList();
            Point intersection = null;
            Vector vector = null;
            List<Hail> transformedHails = null;
            outer: for (var i = -area; i <= area; i++) {
                for (var j = -area; j <= area; j++) {
                    var change = new Vector(-i, -j, 0);
                    transformedHails = hails.stream().map(h -> h.speedUp(change)).toList();
                    intersection = intersection(transformedHails.get(0), transformedHails.get(1));
                    if(intersection == null){
                        continue;
                    }
                    Point hereIntersection = null;
                    for(var k = 2; k < transformedHails.size(); k++){
                        hereIntersection = intersection(transformedHails.get(0), transformedHails.get(k));
                        if(hereIntersection == null || !hereIntersection.equals(intersection)){
                            break;
                        }
                    }
                    if(intersection.equals(hereIntersection)){
                        vector = new Vector(i, j, 0);
                    } else {
                        continue;
                    }
                    if(intersection.equals(hereIntersection)){
                        break outer;
                    }
                }
            }
            var first = transformedHails.get(0);
            var t1 = (intersection.x - transformedHails.get(0).origin.x)/transformedHails.get(0).dir.px;
            var cz1 = first.origin.z + first.dir.pz*(intersection.x - first.origin.x)/first.dir.px;
            var second = transformedHails.get(1);
            var t2 = (intersection.x - transformedHails.get(1).origin.x)/transformedHails.get(1).dir.px;
            var cz2 = second.origin.z + second.dir.pz*(intersection.x - second.origin.x)/second.dir.px;
            var vz = (cz2 - cz1)/(t2 - t1);
            var z_hail = BigInteger.valueOf((long)(cz1 - t1*vz));
            var x1_collision = hails.get(0).origin.x + t1*hails.get(0).dir.px;
            var x_hail = BigInteger.valueOf((long)(x1_collision - t1*vector.px));
            var y1_collision = hails.get(0).origin.y + t1*hails.get(0).dir.py;
            var y_hail = BigInteger.valueOf((long)(y1_collision - t1*vector.py));
            return String.valueOf(x_hail.add(y_hail).add(z_hail));
        });
    }
}