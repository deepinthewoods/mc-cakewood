package ninja.trek.cakewood.tree;

import java.util.random.RandomGenerator;

/** Soft octant scoring lets nearby foliage palettes drift across hard direction boundaries. */
public final class DirectionalPaletteSelector {
    private DirectionalPaletteSelector() {}

    public static RingDirection select(double bearing, RandomGenerator random) {
        RingDirection winner = RingDirection.NORTH;
        double winnerScore = -Double.MAX_VALUE;
        for (RingDirection direction : RingDirection.values()) {
            double difference = angularDistance(bearing, direction.bearing());
            double score = Math.cos(difference) * 2.0 + random.nextDouble(-.52, .52);
            if (score > winnerScore) {
                winnerScore = score;
                winner = direction;
            }
        }
        return winner;
    }

    public static double angularDistance(double first, double second) {
        double difference = Math.abs(first - second) % (Math.PI * 2.0);
        return difference > Math.PI ? Math.PI * 2.0 - difference : difference;
    }
}
