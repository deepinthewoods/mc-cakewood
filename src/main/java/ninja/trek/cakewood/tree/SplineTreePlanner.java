package ninja.trek.cakewood.tree;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import ninja.trek.cakewood.tree.SplineTreePlan.Placement;
import ninja.trek.cakewood.tree.SplineTreePlan.PlacementKind;
import ninja.trek.cakewood.tree.SplineVoxelizer.WoodVoxel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Builds the complete placement plan and performs all collision/chunk/build-height preflight checks. */
public final class SplineTreePlanner {
    private SplineTreePlanner() {}

    public static Optional<SplineTreePlan> create(ServerLevel level, SaplingPattern pattern, long seed) {
        BlockPos center = pattern.center();
        Vec3 base = new Vec3(center.getX() + .5, center.getY() + .18, center.getZ() + .5);
        SplineTreeSkeleton skeleton = SplineTreeSkeletonGenerator.generate(pattern.structuralProfile(), base, seed,
                (x, z) -> findSurface(level, x, z, center.getY()));
        SplineVoxelizer.Result voxelization = SplineVoxelizer.voxelize(skeleton, center.getY());
        if (voxelization.overBudget()) return Optional.empty();

        Map<BlockPos, Placement> placements = new LinkedHashMap<>();
        Set<BlockPos> woodPositions = new HashSet<>();
        for (Map.Entry<BlockPos, WoodVoxel> entry : voxelization.voxels().entrySet()) {
            BlockPos pos = entry.getKey();
            WoodVoxel voxel = entry.getValue();
            if (!level.isInsideBuildHeight(pos) || !level.hasChunkAt(pos)) {
                if (voxel.major()) return Optional.empty();
                continue;
            }
            BlockState existing = level.getBlockState(pos);
            if (!canReplaceWithWood(existing, pos, pattern)) {
                if (voxel.major()) return Optional.empty();
                continue;
            }
            woodPositions.add(pos);
            placements.put(pos, new Placement(voxel.state(), PlacementKind.WOOD));
        }
        if (woodPositions.isEmpty()) return Optional.empty();

        Map<BlockPos, Block> foliageCandidates = createFoliageCandidates(skeleton, pattern, seed,
                SplineVoxelizer.MAX_PLACEMENT_BUDGET - placements.size());
        foliageCandidates.keySet().removeAll(woodPositions);
        foliageCandidates.entrySet().removeIf(entry -> !canPlaceFoliage(level, entry.getKey()));
        Map<BlockPos, Integer> leafDistances = connectedLeafDistances(woodPositions, foliageCandidates.keySet());
        for (Map.Entry<BlockPos, Block> entry : foliageCandidates.entrySet()) {
            Integer distance = leafDistances.get(entry.getKey());
            if (distance == null || distance > 6) continue;
            BlockState state = entry.getValue().defaultBlockState();
            if (state.hasProperty(LeavesBlock.PERSISTENT)) state = state.setValue(LeavesBlock.PERSISTENT, false);
            if (state.hasProperty(LeavesBlock.DISTANCE)) state = state.setValue(LeavesBlock.DISTANCE, distance);
            placements.put(entry.getKey(), new Placement(state, PlacementKind.FOLIAGE));
            if (placements.size() >= SplineVoxelizer.MAX_PLACEMENT_BUDGET) break;
        }
        if (placements.size() > SplineVoxelizer.MAX_PLACEMENT_BUDGET) return Optional.empty();
        return Optional.of(new SplineTreePlan(skeleton, pattern.center(), pattern.consumedSaplings(), placements));
    }

    private static double findSurface(ServerLevel level, double x, double z, int baseY) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos((int) Math.floor(x), baseY + 3,
                (int) Math.floor(z));
        if (!level.hasChunkAt(cursor)) return baseY;
        for (int y = baseY + 3; y >= baseY - 5; y--) {
            cursor.setY(y);
            BlockState state = level.getBlockState(cursor);
            if (!state.isAir() && state.getFluidState().isEmpty() && !state.canBeReplaced()) return y + 1.0;
        }
        return baseY;
    }

    private static boolean canReplaceWithWood(BlockState state, BlockPos pos, SaplingPattern pattern) {
        if (state.hasBlockEntity()) return false;
        Block expected = pattern.consumedSaplings().get(pos);
        if (expected != null && state.getBlock() == expected) return true;
        return state.isAir() || state.is(BlockTags.REPLACEABLE_BY_TREES) || state.canBeReplaced()
                || !state.getFluidState().isEmpty();
    }

    private static boolean canPlaceFoliage(ServerLevel level, BlockPos pos) {
        if (!level.isInsideBuildHeight(pos) || !level.hasChunkAt(pos)) return false;
        BlockState state = level.getBlockState(pos);
        if (state.hasBlockEntity() || !state.getFluidState().isEmpty()) return false;
        return state.isAir() || state.is(BlockTags.LEAVES) || state.is(BlockTags.REPLACEABLE_BY_TREES)
                || state.canBeReplaced();
    }

    private static Map<BlockPos, Block> createFoliageCandidates(SplineTreeSkeleton skeleton,
            SaplingPattern pattern, long seed, int budget) {
        Map<BlockPos, Block> result = new HashMap<>();
        int blobIndex = 0;
        for (FoliageBlob blob : skeleton.foliageBlobs()) {
            Random paletteRandom = new Random(seed ^ blob.seed() ^ (long) blobIndex++ * 0x9E3779B97F4A7C15L);
            RingDirection sourceDirection = DirectionalPaletteSelector.select(blob.bearing(), paletteRandom);
            TreeInputDefinition input = pattern.inputs().get(sourceDirection);
            Block foliage = input.chooseFoliage(RandomSource.create(paletteRandom.nextLong()));
            int minX = (int) Math.floor(blob.center().x - blob.radiusX());
            int maxX = (int) Math.ceil(blob.center().x + blob.radiusX());
            int minY = (int) Math.floor(blob.center().y - blob.radiusY());
            int maxY = (int) Math.ceil(blob.center().y + blob.radiusY());
            int minZ = (int) Math.floor(blob.center().z - blob.radiusZ());
            int maxZ = (int) Math.ceil(blob.center().z + blob.radiusZ());
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        double dx = (x + .5 - blob.center().x) / blob.radiusX();
                        double dy = (y + .5 - blob.center().y) / blob.radiusY();
                        double dz = (z + .5 - blob.center().z) / blob.radiusZ();
                        double noise = signedNoise(x, y, z, blob.seed()) * .16;
                        if (dx * dx + dy * dy + dz * dz <= 1.0 + noise) {
                            result.putIfAbsent(new BlockPos(x, y, z), foliage);
                            if (result.size() >= budget) return result;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static Map<BlockPos, Integer> connectedLeafDistances(Set<BlockPos> wood, Set<BlockPos> leaves) {
        Map<BlockPos, Integer> distance = new HashMap<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        for (BlockPos leaf : leaves) {
            for (Direction direction : Direction.values()) {
                if (wood.contains(leaf.relative(direction))) {
                    distance.put(leaf, 1);
                    queue.add(leaf);
                    break;
                }
            }
        }
        while (!queue.isEmpty()) {
            BlockPos current = queue.remove();
            int nextDistance = distance.get(current) + 1;
            if (nextDistance > 6) continue;
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (leaves.contains(next) && distance.putIfAbsent(next, nextDistance) == null) queue.add(next);
            }
        }
        return distance;
    }

    private static double signedNoise(int x, int y, int z, long seed) {
        long value = seed ^ x * 0x632BE59BD9B4E019L ^ y * 0x9E3779B97F4A7C15L ^ z * 0x94D049BB133111EBL;
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        value ^= value >>> 31;
        return ((value >>> 11) * 0x1.0p-53) * 2.0 - 1.0;
    }
}
