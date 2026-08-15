package ninja.trek.cakewood.api;

import java.util.Comparator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/** Immutable, absolute block placement plan suitable for chunked structure generation. */
public record WorldgenTreePlan(BlockPos origin, Map<BlockPos, Placement> placements) {
    public enum PlacementKind { WOOD, FOLIAGE }

    public record Placement(BlockState state, PlacementKind kind) {
        public Placement {
            if (state == null || kind == null) {
                throw new NullPointerException("Tree placements require a state and kind");
            }
        }
    }

    public WorldgenTreePlan {
        origin = origin.immutable();
        placements = Map.copyOf(placements);
    }

    /** Returns an immutable plan containing only blocks inside {@code bounds}. */
    public WorldgenTreePlan within(BoundingBox bounds) {
        return new WorldgenTreePlan(origin, placements.entrySet().stream()
                .filter(entry -> bounds.isInside(entry.getKey()))
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey, Map.Entry::getValue)));
    }

    /** Applies only this plan's blocks that belong to the supplied structure chunk bounds. */
    public int apply(WorldGenLevel level, BoundingBox bounds) {
        int flags = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
        var ordered = placements.entrySet().stream()
                .filter(entry -> bounds.isInside(entry.getKey()))
                .sorted(Comparator
                        .comparing((Map.Entry<BlockPos, Placement> entry) -> entry.getValue().kind())
                        .thenComparingInt(entry -> entry.getKey().getY())
                        .thenComparingInt(entry -> entry.getKey().getX())
                        .thenComparingInt(entry -> entry.getKey().getZ()))
                .toList();
        for (Map.Entry<BlockPos, Placement> entry : ordered) {
            level.setBlock(entry.getKey(), entry.getValue().state(), flags);
        }
        return ordered.size();
    }
}
