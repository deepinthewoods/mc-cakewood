package ninja.trek.cakewood.tree;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Samples spline tubes into independently merged top/bottom two-pixel coverage masks. */
public final class SplineVoxelizer {
    public static final int MAX_PLACEMENT_BUDGET = 12_000;
    private static final double SAMPLE_SPACING = .18;

    private SplineVoxelizer() {}

    public static Result voxelize(SplineTreeSkeleton skeleton, int minimumY) {
        Map<BlockPos, MutableCoverage> coverage = new HashMap<>();
        for (HermiteSpline spline : skeleton.splines()) {
            int steps = Math.max(2, (int) Math.ceil(spline.approximateLength() / SAMPLE_SPACING));
            for (int step = 0; step <= steps; step++) {
                double t = step / (double) steps;
                addSphereProjection(coverage, spline.evaluate(t), spline.radius(t), spline.major(), minimumY);
            }
            if (coverage.size() > MAX_PLACEMENT_BUDGET) return new Result(Map.of(), true);
        }
        Map<BlockPos, WoodVoxel> result = new HashMap<>();
        coverage.forEach((pos, value) -> {
            if (value.topMask != 0 || value.bottomMask != 0) {
                result.put(pos, new WoodVoxel(
                        PartialBlockQuantizer.toBlockState(value.topMask, value.bottomMask), value.major));
            }
        });
        return new Result(Map.copyOf(result), result.size() > MAX_PLACEMENT_BUDGET);
    }

    private static void addSphereProjection(Map<BlockPos, MutableCoverage> result, Vec3 point,
            double radius, boolean major, int minimumY) {
        int minMicroX = (int) Math.floor((point.x - radius) * 8.0);
        int maxMicroX = (int) Math.floor((point.x + radius) * 8.0);
        int minMicroZ = (int) Math.floor((point.z - radius) * 8.0);
        int maxMicroZ = (int) Math.floor((point.z + radius) * 8.0);
        double radiusSquared = radius * radius;
        for (int microX = minMicroX; microX <= maxMicroX; microX++) {
            double x = (microX + .5) / 8.0;
            double dx = x - point.x;
            for (int microZ = minMicroZ; microZ <= maxMicroZ; microZ++) {
                double z = (microZ + .5) / 8.0;
                double dz = z - point.z;
                double horizontalSquared = dx * dx + dz * dz;
                if (horizontalSquared > radiusSquared) continue;
                double verticalRadius = Math.sqrt(radiusSquared - horizontalSquared);
                int minY = Math.max(minimumY, (int) Math.floor(point.y - verticalRadius));
                int maxY = (int) Math.floor(point.y + verticalRadius);
                int blockX = Math.floorDiv(microX, 8);
                int blockZ = Math.floorDiv(microZ, 8);
                int localX = Math.floorMod(microX, 8);
                int localZ = Math.floorMod(microZ, 8);
                long bit = 1L << (localX + localZ * 8);
                double tubeMinY = point.y - verticalRadius;
                double tubeMaxY = point.y + verticalRadius;
                for (int y = minY; y <= maxY; y++) {
                    MutableCoverage value = result.computeIfAbsent(new BlockPos(blockX, y, blockZ), ignored -> new MutableCoverage());
                    if (tubeMaxY >= y && tubeMinY <= y + .5) value.bottomMask |= bit;
                    if (tubeMaxY >= y + .5 && tubeMinY <= y + 1.0) value.topMask |= bit;
                    value.major |= major;
                }
            }
        }
    }

    public record WoodVoxel(BlockState state, boolean major) {}
    public record Result(Map<BlockPos, WoodVoxel> voxels, boolean overBudget) {}

    private static final class MutableCoverage {
        long topMask;
        long bottomMask;
        boolean major;
    }
}
