import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Day22 {

    record Block(Integer lineNumber, List<Cd> coordinates) {
        public Block fall(List<Block> otherBlocks) {
            var newbottom = new Block(this.lineNumber, coordinates.stream().map(c -> c.fall()).toList());
            if (newbottom.bottom() == 0) {
                return null;
            }
            if (otherBlocks == null || otherBlocks.stream().noneMatch(o -> newbottom.overlaps(o))) {
                return newbottom;
            }
            return null;
        }

        public boolean overlaps(Block other) {
            return !Collections.disjoint(this.coordinates, other.coordinates);
        }

        public int bottom() {
            return this.coordinates.stream().map(c -> c.z).min(Comparator.naturalOrder()).get();
        }

        public int top() {
            return this.coordinates.stream().map(c -> c.z).max(Comparator.naturalOrder()).get() + 1;
        }
    }

    record Cd(int x, int y, int z) {
        public Cd fall() {
            return new Cd(this.x, this.y, this.z - 1);
        }
    }

    public static void main(String[] args) {
        calculate("src/day22/example.txt");
        calculate("src/day22/input.txt");
        calculate_b("src/day22/example.txt");
        calculate_b("src/day22/input.txt");
    }

    private static void calculate(String filename) {
        Util.applyAndPrint(filename, lines -> {
            var blocks = lines.map(line -> toBlock(line, null)).toList();
            var map = blocksMap(blocks);
            blocks = letBlocksFall(map);
            var newMap = blocksMap(blocks);
            var result = blocks.stream().filter(b -> canDisappear(b, newMap)).count();
            return String.valueOf(result);
        });
    }

    private static void calculate_b(String filename) {
        Util.applyAndPrint(filename, lines -> {
            var lineList = lines.toList();
            var blocks = IntStream.range(0, lineList.size()).mapToObj(i -> toBlock(lineList.get(i), i)).toList();
            var map = blocksMap(blocks);
            blocks = letBlocksFall(map);
            var newMap = blocksMap(blocks);
            var result = blocks.stream().mapToInt(b -> numberOfFallingBlocks(b, newMap)).sum();
            return String.valueOf(result);
        });
    }

    private static Block toBlock(String line, Integer lineNumber) {
        var split = line.split("~");
        var begin = Util.split(split[0], ",").map(Integer::parseInt).toList();
        var end = Util.split(split[1], ",").map(Integer::parseInt).toList();
        var coordinates = IntStream.rangeClosed(begin.get(0), end.get(0)).boxed()
                .flatMap(x -> IntStream.rangeClosed(begin.get(1), end.get(1)).boxed().flatMap(
                        y -> IntStream.rangeClosed(begin.get(2), end.get(2)).boxed().map(z -> new Cd(x, y, z))))
                .toList();
        return new Block(lineNumber, coordinates);
    }

    private static List<Block> letBlocksFall(Map<Integer, List<Block>> blocks) {
        var min = blocks.keySet().stream().min(Comparator.naturalOrder()).get();
        var max = blocks.keySet().stream().max(Comparator.naturalOrder()).get();
        var newList = new ArrayList<Block>();
        for (int i = min; i <= max; i++) {
            if (!blocks.containsKey(i)) {
                continue;
            }
            var blockList = new ArrayList<>(blocks.get(i));
            for (var block : blockList) {
                while (true) {
                    blocks.get(i).remove(block);
                    var blockFall = block.fall(getFiveLayersBelow(blocks, block.bottom()));
                    if (blockFall == null) {
                        newList.add(block);
                        blocks.get(block.bottom()).add(block);
                        break;
                    }
                    block = blockFall;
                }
            }
        }
        return newList;
    }

    private static List<Block> getFiveLayersBelow(Map<Integer, List<Block>> blocks, Integer topLayer) {
        return IntStream.range(topLayer - 5, topLayer)
                .filter(j -> blocks.containsKey(j))
                .boxed()
                .flatMap(j -> blocks.get(j).stream())
                .toList();
    }

    private static Map<Integer, List<Block>> blocksMap(List<Block> blocks) {
        var max = blocks.stream().flatMap(b -> b.coordinates().stream()).map(c -> c.z).max(Comparator.naturalOrder())
                .get();
        var map = new HashMap<Integer, List<Block>>();
        for (var i = 1; i <= max; i++)
            map.put(i, new ArrayList<>());
        for (var block : blocks) {
            map.get(block.bottom()).add(block);
        }
        return map;
    }

    private static boolean canDisappear(Block block, Map<Integer, List<Block>> blocks) {
        var fiveLayers = new ArrayList<>(getFiveLayersBelow(blocks, block.top()));
        fiveLayers.remove(block);
        var above = blocks.get(block.top());
        if(above == null || above.isEmpty()){
            return true;
        }
        return above.stream().allMatch(a -> a.fall(fiveLayers) == null);
    }

    private static int numberOfFallingBlocks(Block block, Map<Integer, List<Block>> blocks) {
        var copiedBlocks = deepCopy(blocks);
        copiedBlocks.get(block.bottom()).remove(block);
        return howManyBlocksFall(copiedBlocks);
    }

    private static Map<Integer, List<Block>> deepCopy(Map<Integer, List<Block>> blocks) {
        return blocks.keySet().stream().collect(Collectors.toMap(
            i -> i, 
            i -> new ArrayList<>(blocks.get(i).stream().toList())
        ));
    }

    private static Integer howManyBlocksFall(Map<Integer, List<Block>> blocks) {
        var min = blocks.keySet().stream().min(Comparator.naturalOrder()).get();
        var max = blocks.keySet().stream().max(Comparator.naturalOrder()).get();
        var result = new HashSet<Integer>();
        for (int i = min; i <= max; i++) {
            if (!blocks.containsKey(i)) {
                continue;
            }
            var blockList = new ArrayList<>(blocks.get(i));
            for (var block : blockList) {
                while (true) {
                    blocks.get(i).remove(block);
                    var blockFall = block.fall(getFiveLayersBelow(blocks, block.bottom()));
                    if (blockFall == null) {
                        blocks.get(block.bottom()).add(block);
                        break;
                    }
                    result.add(block.lineNumber());
                    block = blockFall;
                }
            }
        }
        return result.size();
    }
}