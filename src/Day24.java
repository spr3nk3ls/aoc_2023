import java.util.List;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.stream.Stream;

public class Day24 {

    public static void main(String[] args) {
        calculate("src/day24/example.txt", 7L, 27L);
        // 25552 too high, 12778 too low
        calculate("src/day24/input.txt", 200000000000000L, 400000000000000L);
    }

    private static void calculate(String filename, long min, long max) {
        Util.applyAndPrint(filename, fileLines -> {
            var lines = toLines(fileLines, min, max);
            int i = 0;
            for(var line1 : lines){
                for(var line2 : lines){
                    if(!line1.equals(line2) && line1.intersectsLine(line2)){
                            i++;
                    }
                }
            }
            return String.valueOf(i/2);
        });
    }

    private static List<Line2D.Double> toLines(Stream<String> fileLines, long min, long max) {
        Line2D.Double y_min = new Line2D.Double(min, min, min, max);
        Line2D.Double y_max = new Line2D.Double(min, max, max, max);
        Line2D.Double x_min = new Line2D.Double(min, min, max, min);
        Line2D.Double x_max = new Line2D.Double(max, min, max, max);
        var boxAsList = List.of(y_min, y_max, x_min, x_max);
        return fileLines.map(fl -> {
            var split = fl.split(" @ ");
            var start = Util.split(split[0],", ").map(s -> s.strip()).map(Double::parseDouble).toList();
            var startPoint = new Point2D.Double(start.get(0), start.get(1));
            var dir = Util.split(split[1],", ").map(s -> s.strip()).map(Double::parseDouble).toList();
            var intersections = boxAsList.stream().map(b -> intersection(startPoint, b, new double[]{dir.get(0), dir.get(1)})).filter(i -> i != null).toList();
            if(intersections.size() == 1){
                return new Line2D.Double(startPoint, intersections.get(0));
            } else if (intersections.size() == 2){
                return new Line2D.Double(intersections.get(0), intersections.get(1));
            } else {
                throw new RuntimeException();
            }
        }).toList();
    }

    public static Point2D intersection(Point2D origin, Line2D line, double[] dir){
        var v1 = new Point2D.Double(origin.getX() - line.getX1(), origin.getY() - line.getY1());
        var v2 = new Point2D.Double(line.getX2() - line.getX1(), line.getY2() - line.getY1());
        var v3 = new Point2D.Double(-dir[1], dir[0]);

        var dot = v2.x*v3.x + v2.y*v3.y;
        if (Math.abs(dot) < 0.000001)
            return null;
        var t1 = (v2.x*v1.y - v1.x*v2.y) / dot;
        var t2 = (v1.x*v3.x + v1.y*v3.y) / dot;

        if (t1 >= 0.0 && (t2 >= 0.0 && t2 <= 1.0))
            return new Point2D.Double(origin.getX() + t1*dir[0], origin.getY() + t1*dir[1]);

        return null;
    }

    public static Point2D intersection(Line2D a, Line2D b) {
        double x1 = a.getX1(), y1 = a.getY1(), x2 = a.getX2(), y2 = a.getY2(), x3 = b.getX1(), y3 = b.getY1(),
                x4 = b.getX2(), y4 = b.getY2();
        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d == 0) {
            return null;
        }

        double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

        return new Point2D.Double(xi, yi);
    }
}
