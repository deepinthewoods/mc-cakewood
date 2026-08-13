package ninja.trek.cakewood.tree;

/** Clockwise octants around the CakeWood center. */
public enum RingDirection {
    NORTH(0, -1, -Math.PI / 2.0, true),
    NORTHEAST(1, -1, -Math.PI / 4.0, false),
    EAST(1, 0, 0.0, true),
    SOUTHEAST(1, 1, Math.PI / 4.0, false),
    SOUTH(0, 1, Math.PI / 2.0, true),
    SOUTHWEST(-1, 1, Math.PI * 3.0 / 4.0, false),
    WEST(-1, 0, Math.PI, true),
    NORTHWEST(-1, -1, -Math.PI * 3.0 / 4.0, false);

    private final int x;
    private final int z;
    private final double bearing;
    private final boolean cardinal;

    RingDirection(int x, int z, double bearing, boolean cardinal) {
        this.x = x;
        this.z = z;
        this.bearing = bearing;
        this.cardinal = cardinal;
    }

    public int x() { return x; }
    public int z() { return z; }
    public double bearing() { return bearing; }
    public boolean cardinal() { return cardinal; }
}
