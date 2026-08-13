package ninja.trek.cakewood.tree;

import ninja.trek.cakewood.CakeWoodBlock;
import ninja.trek.cakewood.CakeWoodCornerBlock;
import ninja.trek.cakewood.CakeWoodCornerBlock.DiagonalDirection;
import ninja.trek.cakewood.CakeWoodRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/** Converts merged 8x8 (two-pixel) horizontal coverage masks into existing CakeWood half states. */
public final class PartialBlockQuantizer {
    public static final long FULL_MASK = -1L;

    private PartialBlockQuantizer() {}

    public enum ShapeType { EMPTY, FULL, CARDINAL, CORNER }

    public record HalfShape(ShapeType type, int bites, Direction cardinal, DiagonalDirection diagonal) {
        static HalfShape empty() { return new HalfShape(ShapeType.EMPTY, 8, Direction.NORTH, DiagonalDirection.NORTHWEST); }
        static HalfShape full() { return new HalfShape(ShapeType.FULL, 0, Direction.NORTH, DiagonalDirection.NORTHWEST); }
    }

    public static HalfShape quantize(long mask) {
        if (mask == 0L) return HalfShape.empty();
        if (mask == FULL_MASK) return HalfShape.full();
        int minX = 8, minZ = 8, maxX = -1, maxZ = -1;
        int occupied = Long.bitCount(mask);
        for (int z = 0; z < 8; z++) {
            for (int x = 0; x < 8; x++) {
                if ((mask & (1L << (x + z * 8))) != 0) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minZ = Math.min(minZ, z);
                    maxZ = Math.max(maxZ, z);
                }
            }
        }
        int width = maxX - minX + 1;
        int depth = maxZ - minZ + 1;
        int rectangleArea = width * depth;
        if (minX == 0 && maxX == 7 && minZ == 0 && maxZ == 7) return HalfShape.full();
        if (occupied < Math.max(1, rectangleArea / 3)) return HalfShape.full();

        if (width == 8 && (minZ == 0 || maxZ == 7)) {
            Direction facing = minZ == 0 ? Direction.SOUTH : Direction.NORTH;
            return new HalfShape(ShapeType.CARDINAL, 8 - depth, facing, DiagonalDirection.NORTHWEST);
        }
        if (depth == 8 && (minX == 0 || maxX == 7)) {
            Direction facing = minX == 0 ? Direction.EAST : Direction.WEST;
            return new HalfShape(ShapeType.CARDINAL, 8 - width, facing, DiagonalDirection.NORTHWEST);
        }
        boolean anchoredX = minX == 0 || maxX == 7;
        boolean anchoredZ = minZ == 0 || maxZ == 7;
        if (anchoredX && anchoredZ) {
            int size = Math.max(width, depth);
            DiagonalDirection facing;
            if (minX == 0 && minZ == 0) facing = DiagonalDirection.NORTHWEST;
            else if (maxX == 7 && minZ == 0) facing = DiagonalDirection.NORTHEAST;
            else if (maxX == 7) facing = DiagonalDirection.SOUTHEAST;
            else facing = DiagonalDirection.SOUTHWEST;
            return new HalfShape(ShapeType.CORNER, 8 - size, Direction.NORTH, facing);
        }
        return HalfShape.full();
    }

    public static BlockState toBlockState(long topMask, long bottomMask) {
        HalfShape top = quantize(topMask);
        HalfShape bottom = quantize(bottomMask);
        ShapeType selected = selectType(top, bottom);
        if (selected == ShapeType.FULL) return CakeWoodRegistry.CAKE_WOOD_BLOCK.defaultBlockState();
        if (selected == ShapeType.CORNER) {
            return CakeWoodRegistry.CORNER_CAKE_WOOD_BLOCK.defaultBlockState()
                    .setValue(CakeWoodCornerBlock.TOP_BITES, bitesFor(top))
                    .setValue(CakeWoodCornerBlock.BOTTOM_BITES, bitesFor(bottom))
                    .setValue(CakeWoodCornerBlock.TOP_FACING, diagonalFor(top, bottom))
                    .setValue(CakeWoodCornerBlock.BOTTOM_FACING, diagonalFor(bottom, top));
        }
        Direction topFacing = cardinalFor(top, bottom);
        Direction bottomFacing = cardinalFor(bottom, top);
        return CakeWoodRegistry.CAKE_WOOD_BLOCK.defaultBlockState()
                .setValue(CakeWoodBlock.TOP_BITES, bitesFor(top))
                .setValue(CakeWoodBlock.BOTTOM_BITES, bitesFor(bottom))
                .setValue(CakeWoodBlock.TOP_FACING, topFacing)
                .setValue(CakeWoodBlock.BOTTOM_FACING, bottomFacing);
    }

    static ShapeType selectType(HalfShape top, HalfShape bottom) {
        ShapeType first = partialType(top);
        ShapeType second = partialType(bottom);
        if (first != null && second != null && first != second) return ShapeType.FULL;
        if (first != null) return first;
        if (second != null) return second;
        return ShapeType.CARDINAL;
    }

    private static ShapeType partialType(HalfShape shape) {
        return shape.type == ShapeType.CARDINAL || shape.type == ShapeType.CORNER ? shape.type : null;
    }

    private static int bitesFor(HalfShape shape) {
        return switch (shape.type) {
            case EMPTY -> 8;
            case FULL -> 0;
            case CARDINAL, CORNER -> shape.bites;
        };
    }

    private static Direction cardinalFor(HalfShape preferred, HalfShape fallback) {
        if (preferred.type == ShapeType.CARDINAL) return preferred.cardinal;
        if (fallback.type == ShapeType.CARDINAL) return fallback.cardinal;
        return Direction.NORTH;
    }

    private static DiagonalDirection diagonalFor(HalfShape preferred, HalfShape fallback) {
        if (preferred.type == ShapeType.CORNER) return preferred.diagonal;
        if (fallback.type == ShapeType.CORNER) return fallback.diagonal;
        return DiagonalDirection.NORTHWEST;
    }
}
