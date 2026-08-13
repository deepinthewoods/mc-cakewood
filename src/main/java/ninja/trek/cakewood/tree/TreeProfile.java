package ninja.trek.cakewood.tree;

import java.util.random.RandomGenerator;

/** Trait-based structural input for the procedural tree generator. */
public record TreeProfile(
        String id,
        Topology topology,
        IntRange height,
        DoubleRange baseRadius,
        DoubleRange topRadius,
        DoubleRange trunkCurvature,
        DoubleRange branchStart,
        IntRange primaryBranches,
        DoubleRange branchLength,
        DoubleRange branchRise,
        DoubleRange branchDroop,
        DoubleRange branchCurvature,
        DoubleRange splitChance,
        DoubleRange apicalDominance,
        DoubleRange crownRadius,
        DoubleRange crownVerticalScale,
        DoubleRange foliageRadius,
        IntRange rootCount,
        DoubleRange rootLength,
        DoubleRange rootDrop
) {
    public enum Topology {
        NATIVE, BROAD, TIERED, UPRIGHT, SPREADING, FLAT, DENSE, ROOT_HEAVY, DROOPING, PALE, AZALEA
    }

    public record DoubleRange(double min, double max) {
        public DoubleRange {
            if (!Double.isFinite(min) || !Double.isFinite(max) || min > max) {
                throw new IllegalArgumentException("Invalid range " + min + ".." + max);
            }
        }

        public double sample(RandomGenerator random) {
            return min == max ? min : random.nextDouble(min, max);
        }

        public double midpoint() {
            return (min + max) * 0.5;
        }

        public DoubleRange softlyToward(double target, double amount) {
            double center = midpoint() + (target - midpoint()) * amount;
            double halfWidth = (max - min) * 0.5;
            double boundedCenter = Math.max(min, Math.min(max, center));
            return new DoubleRange(
                    Math.max(min, boundedCenter - halfWidth * 0.7),
                    Math.min(max, boundedCenter + halfWidth * 0.7));
        }
    }

    public record IntRange(int min, int max) {
        public IntRange {
            if (min > max) {
                throw new IllegalArgumentException("Invalid range " + min + ".." + max);
            }
        }

        public int sample(RandomGenerator random) {
            return min == max ? min : random.nextInt(min, max + 1);
        }

        public double midpoint() {
            return (min + max) * 0.5;
        }

        public IntRange softlyToward(double target, double amount) {
            double center = midpoint() + (target - midpoint()) * amount;
            double halfWidth = (max - min) * 0.35;
            int nextMin = Math.max(min, (int) Math.round(center - halfWidth));
            int nextMax = Math.min(max, (int) Math.round(center + halfWidth));
            return new IntRange(Math.min(nextMin, nextMax), Math.max(nextMin, nextMax));
        }
    }
}
