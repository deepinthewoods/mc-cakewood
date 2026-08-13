package ninja.trek.cakewood.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import ninja.trek.cakewood.tree.HermiteSpline.SegmentRole;
import ninja.trek.cakewood.tree.TreeProfile.Topology;
import net.minecraft.world.phys.Vec3;

/** Seeded topology pass for trunks, continuously attached branches, surface roots, and foliage anchors. */
public final class SplineTreeSkeletonGenerator {
    private static final double GOLDEN_ANGLE = Math.PI * (3.0 - Math.sqrt(5.0));
    private static final int MAX_SPLINES = 256;

    private SplineTreeSkeletonGenerator() {}

    public static SplineTreeSkeleton generate(TreeProfile profile, Vec3 base, long seed,
            DoubleBinaryOperator surfaceHeight) {
        Random random = new Random(seed);
        List<HermiteSpline> splines = new ArrayList<>();
        List<FoliageBlob> foliage = new ArrayList<>();
        int height = profile.height().sample(random);
        double baseRadius = profile.baseRadius().sample(random);
        double topRadius = profile.topRadius().sample(random);
        double curve = profile.trunkCurvature().sample(random);
        double curveAngle = random.nextDouble(0, Math.PI * 2.0);
        Vec3 trunkEnd = base.add(Math.cos(curveAngle) * curve * height * .22, height,
                Math.sin(curveAngle) * curve * height * .22);
        Vec3 trunkStartTangent = new Vec3(Math.cos(curveAngle) * curve * height * .28, height * 1.03,
                Math.sin(curveAngle) * curve * height * .28);
        Vec3 trunkEndTangent = new Vec3(-Math.sin(curveAngle) * curve * 2.0, height * .72,
                Math.cos(curveAngle) * curve * 2.0);
        HermiteSpline trunk = new HermiteSpline(base, trunkEnd, trunkStartTangent, trunkEndTangent,
                baseRadius, topRadius, SegmentRole.TRUNK, true);
        splines.add(trunk);

        int branchCount = profile.primaryBranches().sample(random);
        double branchStart = profile.branchStart().sample(random);
        double apical = profile.apicalDominance().sample(random);
        for (int i = 0; i < branchCount && splines.size() < MAX_SPLINES; i++) {
            double sequence = (i + .25 + random.nextDouble() * .5) / branchCount;
            double t = branchStart + (0.90 - branchStart) * sequence;
            if (profile.topology() == Topology.TIERED) {
                t = branchStart + (0.88 - branchStart) * (Math.round(sequence * 5.0) / 5.0);
            } else if (profile.topology() == Topology.FLAT) {
                t = .58 + sequence * .28;
            }
            t = Math.max(.18, Math.min(.92, t));
            double angle = i * GOLDEN_ANGLE + random.nextDouble(-.32, .32);
            double length = profile.branchLength().sample(random) * (1.0 - apical * t * .30);
            double rise = profile.branchRise().sample(random);
            double droop = profile.branchDroop().sample(random);
            if (profile.topology() == Topology.DROOPING) droop += .16;
            if (profile.topology() == Topology.FLAT) rise *= .35;
            double relativeHeight = clamp((t - branchStart) / Math.max(.01, .90 - branchStart));
            double lowerWeight = 1.0 - relativeHeight;
            // Lower limbs carry more length and weight. They still leave the trunk
            // with an upward shoulder, then settle into a downward outer arc.
            length *= 1.0 + lowerWeight * .10;
            double weightedDroop = droop + lowerWeight * .18;
            Vec3 outward = new Vec3(Math.cos(angle), 0, Math.sin(angle));
            Vec3 start = trunk.evaluate(t);
            Vec3 end = start.add(outward.scale(length)).add(0, length * (rise - weightedDroop * .42), 0);
            Vec3 continuous = trunk.derivative(t).normalize().scale(length * .10);
            double shoulderLift = .15 + (1.0 - lowerWeight) * .08;
            Vec3 startTangent = continuous.add(outward.scale(length * .30))
                    .add(0, length * shoulderLift, 0);
            double bend = profile.branchCurvature().sample(random) * length;
            Vec3 side = new Vec3(-Math.sin(angle), 0, Math.cos(angle));
            Vec3 endTangent = outward.scale(length * .58).add(side.scale(random.nextDouble(-bend, bend)))
                    .add(0, -(weightedDroop + lowerWeight * .10) * length, 0);
            double startRadius = Math.max(.48,
                    baseRadius * (.66 - t * .22) * random.nextDouble(.90, 1.11));
            double endRadius = Math.max(.20, topRadius * random.nextDouble(.60, .82));
            HermiteSpline branch = new HermiteSpline(start, end, startTangent, endTangent,
                    startRadius, endRadius, SegmentRole.PRIMARY_BRANCH, true);
            splines.add(branch);
            addBlob(foliage, branch.evaluate(.72), profile, angle, random, .86);
            addBlob(foliage, end, profile, angle, random, 1.0);

            if (random.nextDouble() < profile.splitChance().sample(random) && splines.size() < MAX_SPLINES) {
                double splitT = random.nextDouble(.50, .72);
                Vec3 splitStart = branch.evaluate(splitT);
                double splitAngle = angle + (random.nextBoolean() ? 1 : -1) * random.nextDouble(.42, .82);
                Vec3 splitOut = new Vec3(Math.cos(splitAngle), 0, Math.sin(splitAngle));
                double splitLength = length * random.nextDouble(.38, .58);
                Vec3 splitEnd = splitStart.add(splitOut.scale(splitLength))
                        .add(0, splitLength * (.05 - weightedDroop * .48), 0);
                Vec3 parentFlow = branch.derivative(splitT).normalize().scale(splitLength * .16);
                Vec3 splitStartTangent = new Vec3(parentFlow.x, 0, parentFlow.z)
                        .add(splitOut.scale(splitLength * .28))
                        .add(0, splitLength * .14, 0);
                HermiteSpline secondary = new HermiteSpline(splitStart, splitEnd,
                        splitStartTangent,
                        splitOut.scale(splitLength * .52).add(0, -weightedDroop * splitLength, 0),
                        Math.max(.24, startRadius * .56), Math.max(.15, endRadius * .60),
                        SegmentRole.SECONDARY_BRANCH, false);
                splines.add(secondary);
                addBlob(foliage, splitEnd, profile, splitAngle, random, .88);
            }
        }

        double crownRadius = profile.crownRadius().sample(random);
        double crownScale = profile.crownVerticalScale().sample(random);
        addBlob(foliage, trunkEnd.add(0, -crownRadius * .15, 0), profile, curveAngle, random, 1.12);
        foliage.add(new FoliageBlob(trunkEnd.add(0, crownRadius * .12, 0), crownRadius * .58,
                crownRadius * .34 * crownScale, crownRadius * .58, curveAngle, random.nextLong()));

        int roots = profile.rootCount().sample(random);
        for (int root = 0; root < roots && splines.size() < MAX_SPLINES; root++) {
            double angle = root * (Math.PI * 2.0 / roots) + random.nextDouble(-.28, .28);
            double length = profile.rootLength().sample(random);
            double drop = profile.rootDrop().sample(random);
            int segments = Math.max(2, (int) Math.ceil(length / 3.0));
            Vec3 previous = base.add(Math.cos(angle) * .35, .18, Math.sin(angle) * .35);
            double previousRadius = baseRadius * .72;
            for (int segment = 1; segment <= segments && splines.size() < MAX_SPLINES; segment++) {
                double fraction = segment / (double) segments;
                double distance = length * fraction;
                double winding = Math.sin(fraction * Math.PI * 1.7 + root) * .35;
                double x = base.x + Math.cos(angle + winding * .16) * distance;
                double z = base.z + Math.sin(angle + winding * .16) * distance;
                double surface = surfaceHeight.applyAsDouble(x, z);
                Vec3 next = new Vec3(x, surface + .22 - drop * fraction, z);
                Vec3 delta = next.subtract(previous);
                double radius = Math.max(.16, baseRadius * .72 * (1.0 - fraction * .86));
                splines.add(new HermiteSpline(previous, next, delta.scale(.92), delta.scale(.92),
                        previousRadius, radius, SegmentRole.ROOT, false));
                previous = next;
                previousRadius = radius;
            }
        }
        return new SplineTreeSkeleton(splines, foliage, height);
    }

    public static SplineTreeSkeleton generateFlat(TreeProfile profile, long seed) {
        return generate(profile, new Vec3(.5, .2, .5), seed, (x, z) -> 0.0);
    }

    private static void addBlob(List<FoliageBlob> foliage, Vec3 center, TreeProfile profile,
            double bearing, Random random, double scale) {
        double radius = profile.foliageRadius().sample(random) * scale;
        double vertical = switch (profile.topology()) {
            case TIERED -> .62;
            case FLAT -> .42;
            case DROOPING -> .78;
            default -> .70;
        };
        foliage.add(new FoliageBlob(center, radius, radius * vertical, radius,
                bearing + random.nextDouble(-.18, .18), random.nextLong()));
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
