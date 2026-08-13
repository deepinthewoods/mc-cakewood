package ninja.trek.cakewood.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ninja.trek.cakewood.CakeWoodCornerBlock.DiagonalDirection;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

class PartialBlockQuantizerTest {
    @Test
    void quantizesCardinalCoverageInTwoPixelSteps() {
        long northHalf = rectangle(0, 7, 0, 3);
        PartialBlockQuantizer.HalfShape shape = PartialBlockQuantizer.quantize(northHalf);
        assertEquals(PartialBlockQuantizer.ShapeType.CARDINAL, shape.type());
        assertEquals(4, shape.bites());
        assertEquals(Direction.SOUTH, shape.cardinal());
    }

    @Test
    void quantizesCornerCoverageAndRejectsInteriorGeometry() {
        PartialBlockQuantizer.HalfShape corner = PartialBlockQuantizer.quantize(rectangle(5, 7, 5, 7));
        assertEquals(PartialBlockQuantizer.ShapeType.CORNER, corner.type());
        assertEquals(5, corner.bites());
        assertEquals(DiagonalDirection.SOUTHEAST, corner.diagonal());
        assertEquals(PartialBlockQuantizer.ShapeType.FULL,
                PartialBlockQuantizer.quantize(rectangle(2, 4, 2, 4)).type());
    }

    @Test
    void keepsCornersUnlessTheTwoHalvesNeedIncompatibleBlockFamilies() {
        var corner = PartialBlockQuantizer.quantize(rectangle(5, 7, 5, 7));
        var full = PartialBlockQuantizer.quantize(PartialBlockQuantizer.FULL_MASK);
        var cardinal = PartialBlockQuantizer.quantize(rectangle(0, 7, 0, 3));

        assertEquals(PartialBlockQuantizer.ShapeType.CORNER,
                PartialBlockQuantizer.selectType(corner, full));
        assertEquals(PartialBlockQuantizer.ShapeType.FULL,
                PartialBlockQuantizer.selectType(corner, cardinal));
    }

    private static long rectangle(int minX, int maxX, int minZ, int maxZ) {
        long mask = 0;
        for (int z = minZ; z <= maxZ; z++) {
            for (int x = minX; x <= maxX; x++) mask |= 1L << (x + z * 8);
        }
        return mask;
    }
}
