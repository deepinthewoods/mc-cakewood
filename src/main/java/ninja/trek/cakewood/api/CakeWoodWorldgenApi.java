package ninja.trek.cakewood.api;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import ninja.trek.cakewood.api.WorldgenTreePlan.Placement;
import ninja.trek.cakewood.api.WorldgenTreePlan.PlacementKind;
import ninja.trek.cakewood.tree.FoliageBlob;
import ninja.trek.cakewood.tree.SplineTreeSkeleton;
import ninja.trek.cakewood.tree.SplineTreeSkeletonGenerator;
import ninja.trek.cakewood.tree.SplineVoxelizer;
import ninja.trek.cakewood.tree.TreeProfile;
import ninja.trek.cakewood.tree.TreeProfiles;

/** Deterministic tree planning entry point for structures and other world generation. */
public final class CakeWoodWorldgenApi {
    private CakeWoodWorldgenApi() {
    }

    /**
     * Creates a complete placement plan without saplings, a loaded chunk, or a
     * {@code ServerLevel}. Surface heights are the Y coordinate immediately
     * above the solid surface at each sampled world X/Z position.
     */
    public static WorldgenTreePlan createPlan(
            BlockPos origin,
            String profileId,
            Block foliage,
            long seed,
            DoubleBinaryOperator surfaceHeight
    ) {
        TreeProfile profile = TreeProfiles.get(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown CakeWood tree profile: " + profileId));
        return createPlan(origin, profile, foliage, seed, surfaceHeight);
    }

    /** Typed-profile overload for integrations that customize CakeWood traits. */
    public static WorldgenTreePlan createPlan(
            BlockPos origin,
            TreeProfile profile,
            Block foliage,
            long seed,
            DoubleBinaryOperator surfaceHeight
    ) {
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(foliage, "foliage");
        Objects.requireNonNull(surfaceHeight, "surfaceHeight");

        Vec3 base = new Vec3(origin.getX() + 0.5, origin.getY() + 0.18, origin.getZ() + 0.5);
        SplineTreeSkeleton skeleton = SplineTreeSkeletonGenerator.generate(
                profile, base, seed, surfaceHeight);
        SplineVoxelizer.Result voxelization = SplineVoxelizer.voxelize(skeleton, origin.getY() - 8);
        if (voxelization.overBudget()) {
            throw new IllegalStateException("CakeWood tree exceeds the placement budget");
        }

        Map<BlockPos, Placement> result = new LinkedHashMap<>();
        Set<BlockPos> wood = new HashSet<>();
        voxelization.voxels().forEach((pos, voxel) -> {
            BlockPos immutable = pos.immutable();
            wood.add(immutable);
            result.put(immutable, new Placement(voxel.state(), PlacementKind.WOOD));
        });

        Map<BlockPos, BlockState> foliageCandidates = foliageCandidates(
                skeleton, foliage, seed, SplineVoxelizer.MAX_PLACEMENT_BUDGET - result.size());
        foliageCandidates.keySet().removeAll(wood);
        Map<BlockPos, Integer> distances = connectedLeafDistances(wood, foliageCandidates.keySet());
        for (Map.Entry<BlockPos, BlockState> entry : foliageCandidates.entrySet()) {
            Integer distance = distances.get(entry.getKey());
            if (distance == null || distance > LeavesBlock.DECAY_DISTANCE) {
                continue;
            }
            BlockState state = entry.getValue();
            if (state.hasProperty(LeavesBlock.PERSISTENT)) {
                state = state.setValue(LeavesBlock.PERSISTENT, false);
            }
            if (state.hasProperty(LeavesBlock.DISTANCE)) {
                state = state.setValue(LeavesBlock.DISTANCE, distance);
            }
            result.put(entry.getKey(), new Placement(state, PlacementKind.FOLIAGE));
            if (result.size() >= SplineVoxelizer.MAX_PLACEMENT_BUDGET) {
                break;
            }
        }
        return new WorldgenTreePlan(origin, result);
    }

    private static Map<BlockPos, BlockState> foliageCandidates(
            SplineTreeSkeleton skeleton,
            Block foliage,
            long seed,
            int budget
    ) {
        Map<BlockPos, BlockState> result = new HashMap<>();
        int blobIndex = 0;
        for (FoliageBlob blob : skeleton.foliageBlobs()) {
            long blobSeed = seed ^ blob.seed() ^ (long) blobIndex++ * 0x9E3779B97F4A7C15L;
            Random random = new Random(blobSeed);
            int minX = (int) Math.floor(blob.center().x - blob.radiusX());
            int maxX = (int) Math.ceil(blob.center().x + blob.radiusX());
            int minY = (int) Math.floor(blob.center().y - blob.radiusY());
            int maxY = (int) Math.ceil(blob.center().y + blob.radiusY());
            int minZ = (int) Math.floor(blob.center().z - blob.radiusZ());
            int maxZ = (int) Math.ceil(blob.center().z + blob.radiusZ());
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        double dx = (x + 0.5 - blob.center().x) / blob.radiusX();
                        double dy = (y + 0.5 - blob.center().y) / blob.radiusY();
                        double dz = (z + 0.5 - blob.center().z) / blob.radiusZ();
                        double noise = signedNoise(x, y, z, random.nextLong()) * 0.16;
                        if (dx * dx + dy * dy + dz * dz <= 1.0 + noise) {
                            result.putIfAbsent(new BlockPos(x, y, z), foliage.defaultBlockState());
                            if (result.size() >= budget) {
                                return result;
                            }
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
            if (nextDistance > LeavesBlock.DECAY_DISTANCE) {
                continue;
            }
            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (leaves.contains(next) && distance.putIfAbsent(next, nextDistance) == null) {
                    queue.add(next);
                }
            }
        }
        return distance;
    }

    private static double signedNoise(int x, int y, int z, long seed) {
        long value = seed ^ x * 0x632BE59BD9B4E019L
                ^ y * 0x9E3779B97F4A7C15L ^ z * 0x94D049BB133111EBL;
        value ^= value >>> 30;
        value *= 0xBF58476D1CE4E5B9L;
        value ^= value >>> 27;
        value *= 0x94D049BB133111EBL;
        value ^= value >>> 31;
        return ((value >>> 11) * 0x1.0p-53) * 2.0 - 1.0;
    }
}
