import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

public class Day24 {

    record Line(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2, BigDecimal slope) {
        public boolean intersects(Line other){
            return linesIntersect(x1, y1, x2, y2, other.x1, other.y1, other.x2, other.y2);
        }

        public static boolean linesIntersect(BigDecimal x1, BigDecimal y1,
        BigDecimal x2, BigDecimal y2,
        BigDecimal x3, BigDecimal y3,
        BigDecimal x4, BigDecimal y4) {
            return ((relativeCCW(x1, y1, x2, y2, x3, y3) *
                    relativeCCW(x1, y1, x2, y2, x4, y4) <= 0)
                    && (relativeCCW(x3, y3, x4, y4, x1, y1) *
                            relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
        }

        public static int relativeCCW(BigDecimal x1, BigDecimal y1,
        BigDecimal x2, BigDecimal y2,
        BigDecimal px, BigDecimal py) {
            x2 = x2.subtract(x1);
            y2 = y2.subtract(y1);
            px = px.subtract(x1);
            py = py.subtract(y1);
            var ccw = px.multiply(y2).subtract(py.multiply(x2));
            // double ccw = px * y2 - py * x2;
            if(ccw.equals(BigDecimal.ZERO)){
                ccw = px.multiply(x2).add(py.multiply(y2));
                System.out.println(ccw);
                if(ccw.compareTo(BigDecimal.ZERO) > 0){
                    px = px.subtract(x2);
                    py = py.subtract(y2);
                    ccw = px.multiply(x2).add(py.multiply(y2));
                    if(ccw.compareTo(BigDecimal.ZERO) < 0){
                        ccw = BigDecimal.ZERO;
                    }
                }
            }
            return ccw.compareTo(BigDecimal.ZERO) < 0 ? -1 : (ccw.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
        }
    }

    public static void main(String[] args) {
        calculate("src/day24/example.txt", BigDecimal.valueOf(7), BigDecimal.valueOf(27));
        // 25552 too high, 12778 too low
        calculate("src/day24/input.txt", BigDecimal.valueOf(200000000000000L), BigDecimal.valueOf(400000000000000L));
    }

    private static void calculate(String filename, BigDecimal min, BigDecimal max) {
        Util.applyAndPrint(filename, fileLines -> {
            var lines = toLines(fileLines, min, max);
            int i = 0;
            for(var line1 : lines){
                for(var line2 : lines){
                    if(!line1.equals(line2) && line1.intersects(line2)){
                        // var slopeDifference = line1.slope.abs().subtract(line2.slope.abs());
                        // System.out.println(slopeDifference);
                        // if(slopeDifference.abs().compareTo(BigDecimal.valueOf(0.001)) > 0)
                            i++;
                    }
                }
            }
            return String.valueOf(i/2);
        });
    }

    private static List<Line> toLines(Stream<String> fileLines, BigDecimal min, BigDecimal max) {
        return fileLines.map(fl -> {
            var split = fl.split(" @ ");
            var start = Util.split(split[0],", ").map(s -> s.strip()).map(Long::parseLong).map(BigDecimal::valueOf).toList();
            var dir = Util.split(split[1],", ").map(s -> s.strip()).map(Long::parseLong).map(BigDecimal::valueOf).toList();
            var slope = dir.get(1).divide(dir.get(0), 10, RoundingMode.DOWN);
            var right = dir.get(0).compareTo(BigDecimal.ZERO) > 0;
            var y_cross = right ? max : min;
            var y = y_cross.subtract(start.get(0)).multiply(slope).add(start.get(1));
            if(y.compareTo(min) > 0 && y.compareTo(max) < 0)
                return new Line(start.get(0), start.get(1), right ? max : min, y, slope); 
            var up = dir.get(1).compareTo(BigDecimal.ZERO) > 0;
            var x_cross = up ? max : min;
            var x = x_cross.subtract(start.get(1)).divide(slope, 10, RoundingMode.DOWN).add(start.get(0));
                return new Line(start.get(0), start.get(1), x, up ? max : min, slope); 
        }).toList();
    }
}
