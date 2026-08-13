package ninja.trek.cakewood.tree;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Immutable preflight result: spline skeleton, foliage anchors, and final absolute placements. */
public record SplineTreePlan(
        SplineTreeSkeleton skeleton,
        BlockPos center,
        Map<BlockPos, Block> consumedSaplings,
        Map<BlockPos, Placement> placements
) {
    public enum PlacementKind { WOOD, FOLIAGE }
    public record Placement(BlockState state, PlacementKind kind) {}

    public SplineTreePlan {
        center = center.immutable();
        consumedSaplings = Map.copyOf(consumedSaplings);
        placements = Map.copyOf(placements);
    }

    /** Revalidates the ring, consumes it without drops, then places wood before foliage. */
    public boolean apply(ServerLevel level) {
        for (Map.Entry<BlockPos, Block> entry : consumedSaplings.entrySet()) {
            Block actual = level.getBlockState(entry.getKey()).getBlock();
            if (entry.getKey().equals(center)) {
                if (actual != entry.getValue() && !level.getBlockState(entry.getKey()).canBeReplaced()) return false;
            } else if (actual != entry.getValue()) {
                return false;
            }
        }

        int quietFlags = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
        consumedSaplings.keySet().forEach(pos -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), quietFlags));
        List<Map.Entry<BlockPos, Placement>> ordered = placements.entrySet().stream()
                .sorted(Comparator.comparing((Map.Entry<BlockPos, Placement> entry) -> entry.getValue().kind())
                        .thenComparingInt(entry -> entry.getKey().getY())
                        .thenComparingInt(entry -> entry.getKey().getX())
                        .thenComparingInt(entry -> entry.getKey().getZ()))
                .toList();
        for (Map.Entry<BlockPos, Placement> entry : ordered) {
            level.setBlock(entry.getKey(), entry.getValue().state(), quietFlags);
        }
        return true;
    }
}
