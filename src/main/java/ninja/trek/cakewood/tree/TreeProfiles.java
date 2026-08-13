package ninja.trek.cakewood.tree;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import ninja.trek.cakewood.tree.TreeProfile.DoubleRange;
import ninja.trek.cakewood.tree.TreeProfile.IntRange;
import ninja.trek.cakewood.tree.TreeProfile.Topology;

/** Built-in profiles. Mixed cardinals retain native topology with a deliberately small trait bias. */
public final class TreeProfiles {
    private static final Map<String, TreeProfile> PROFILES = new LinkedHashMap<>();

    public static final TreeProfile NATIVE = add(profile("native", Topology.NATIVE,
            26, 32, 1.42, 1.62, .34, .52, .20, .50, .34, .47, 12, 16,
            8.5, 12.2, .08, .34, .05, .28, .28, .64, .28, .48, .22, .42,
            10.0, 13.0, .70, 1.05, 2.3, 3.2, 9, 13, 6.0, 10.0, .15, .55));

    static {
        add(profile("oak", Topology.BROAD, 24, 29, 1.40, 1.58, .35, .48, .10, .30,
                .38, .51, 8, 12, 7.0, 10.5, .08, .28, .04, .18, .18, .40, .14, .28,
                .28, .52, 9.0, 12.0, .72, 1.00, 2.4, 3.2, 6, 9, 4.5, 7.0, .10, .38));
        add(profile("spruce", Topology.TIERED, 27, 32, 1.38, 1.55, .28, .42, .05, .20,
                .30, .42, 10, 14, 5.2, 8.0, -.10, .08, .05, .24, .10, .25, .10, .22,
                .70, .90, 7.8, 10.0, 1.25, 1.75, 1.9, 2.7, 5, 7, 3.5, 5.5, .08, .30));
        add(profile("birch", Topology.UPRIGHT, 26, 31, 1.35, 1.50, .30, .44, .06, .22,
                .46, .60, 7, 10, 5.2, 7.5, .22, .48, .02, .13, .12, .28, .10, .20,
                .58, .78, 7.0, 9.4, 1.05, 1.45, 2.0, 2.7, 5, 7, 3.8, 5.8, .08, .28));
        add(profile("jungle", Topology.SPREADING, 28, 32, 1.48, 1.64, .34, .50, .14, .36,
                .40, .52, 9, 13, 9.0, 12.2, .16, .42, .06, .24, .20, .48, .24, .42,
                .34, .58, 10.5, 13.0, .75, 1.12, 2.5, 3.3, 7, 10, 5.0, 8.0, .14, .48));
        add(profile("acacia", Topology.FLAT, 24, 28, 1.40, 1.58, .32, .48, .20, .45,
                .52, .65, 6, 9, 9.0, 12.5, -.04, .14, .00, .10, .12, .30, .20, .36,
                .30, .50, 11.0, 13.0, .46, .72, 2.2, 3.0, 6, 8, 5.0, 7.5, .10, .35));
        add(profile("dark_oak", Topology.DENSE, 24, 29, 1.50, 1.68, .38, .54, .12, .34,
                .32, .46, 11, 15, 7.0, 10.0, .05, .28, .04, .18, .18, .42, .22, .38,
                .20, .42, 9.5, 12.5, .82, 1.16, 2.7, 3.5, 8, 11, 4.8, 7.4, .10, .38));
        add(profile("mangrove", Topology.ROOT_HEAVY, 25, 30, 1.45, 1.62, .34, .50, .16, .38,
                .38, .52, 9, 13, 7.5, 10.5, .06, .28, .08, .25, .22, .48, .22, .40,
                .28, .52, 9.5, 12.0, .80, 1.22, 2.4, 3.3, 12, 16, 7.0, 11.0, .35, .85));
        add(profile("cherry", Topology.DROOPING, 25, 30, 1.38, 1.56, .32, .48, .16, .38,
                .43, .57, 9, 13, 8.0, 11.2, .14, .36, .22, .50, .22, .48, .24, .42,
                .28, .50, 10.0, 13.0, .72, 1.00, 2.4, 3.2, 6, 9, 4.5, 7.0, .10, .36));
        add(profile("pale_oak", Topology.PALE, 25, 30, 1.44, 1.62, .34, .50, .18, .42,
                .37, .50, 10, 14, 7.5, 10.8, .02, .24, .08, .24, .25, .52, .24, .42,
                .24, .48, 10.0, 12.8, .78, 1.08, 2.5, 3.3, 8, 11, 5.0, 8.0, .12, .42));
        add(profile("azalea", Topology.AZALEA, 24, 27, 1.38, 1.54, .36, .52, .10, .28,
                .48, .60, 8, 11, 7.0, 9.8, .04, .24, .08, .24, .20, .42, .18, .34,
                .28, .50, 9.0, 11.5, .68, .92, 2.6, 3.4, 8, 11, 5.0, 8.2, .18, .55));
        add(profile("flowering_azalea", Topology.AZALEA, 24, 28, 1.38, 1.55, .36, .52, .12, .30,
                .47, .59, 9, 12, 7.2, 10.0, .02, .23, .10, .27, .22, .44, .20, .36,
                .26, .48, 9.2, 11.8, .66, .92, 2.7, 3.5, 8, 12, 5.2, 8.5, .18, .58));
    }

    private TreeProfiles() {}

    public static Optional<TreeProfile> get(String id) {
        return Optional.ofNullable(PROFILES.get(id));
    }

    public static Collection<TreeProfile> all() {
        return List.copyOf(PROFILES.values());
    }

    public static TreeProfile resolveCardinals(List<String> cardinalProfileIds) {
        if (cardinalProfileIds.size() != 4) {
            throw new IllegalArgumentException("Exactly four cardinal profiles are required");
        }
        String first = cardinalProfileIds.getFirst();
        if (cardinalProfileIds.stream().allMatch(first::equals)) {
            return get(first).orElse(NATIVE);
        }
        List<TreeProfile> inputs = cardinalProfileIds.stream().map(id -> get(id).orElse(NATIVE)).toList();
        return mixedNative(inputs);
    }

    public static TreeProfile mixedNative(List<TreeProfile> inputs) {
        if (inputs.isEmpty()) return NATIVE;
        double height = average(inputs, p -> p.height().midpoint());
        double curvature = average(inputs, p -> p.trunkCurvature().midpoint());
        double branches = average(inputs, p -> p.primaryBranches().midpoint());
        double length = average(inputs, p -> p.branchLength().midpoint());
        double droop = average(inputs, p -> p.branchDroop().midpoint());
        double crown = average(inputs, p -> p.crownRadius().midpoint());
        double roots = average(inputs, p -> p.rootLength().midpoint());
        return new TreeProfile("native_mixed", Topology.NATIVE,
                NATIVE.height().softlyToward(height, .18), NATIVE.baseRadius(), NATIVE.topRadius(),
                NATIVE.trunkCurvature().softlyToward(curvature, .18), NATIVE.branchStart(),
                NATIVE.primaryBranches().softlyToward(branches, .18),
                NATIVE.branchLength().softlyToward(length, .18), NATIVE.branchRise(),
                NATIVE.branchDroop().softlyToward(droop, .18), NATIVE.branchCurvature(),
                NATIVE.splitChance(), NATIVE.apicalDominance(),
                NATIVE.crownRadius().softlyToward(crown, .18), NATIVE.crownVerticalScale(),
                NATIVE.foliageRadius(), NATIVE.rootCount(),
                NATIVE.rootLength().softlyToward(roots, .18), NATIVE.rootDrop());
    }

    private static double average(List<TreeProfile> profiles, java.util.function.ToDoubleFunction<TreeProfile> value) {
        return profiles.stream().mapToDouble(value).average().orElse(0.0);
    }

    private static TreeProfile add(TreeProfile profile) {
        PROFILES.put(profile.id(), profile);
        return profile;
    }

    private static TreeProfile profile(String id, Topology topology, int heightMin, int heightMax,
            double baseMin, double baseMax, double topMin, double topMax,
            double curveMin, double curveMax, double startMin, double startMax,
            int branchMin, int branchMax, double lengthMin, double lengthMax,
            double riseMin, double riseMax, double droopMin, double droopMax,
            double branchCurveMin, double branchCurveMax, double splitMin, double splitMax,
            double apicalMin, double apicalMax, double crownMin, double crownMax,
            double crownScaleMin, double crownScaleMax, double foliageMin, double foliageMax,
            int rootMin, int rootMax, double rootLengthMin, double rootLengthMax,
            double rootDropMin, double rootDropMax) {
        return new TreeProfile(id, topology, new IntRange(heightMin, heightMax),
                new DoubleRange(baseMin, baseMax), new DoubleRange(topMin, topMax),
                new DoubleRange(curveMin, curveMax), new DoubleRange(startMin, startMax),
                new IntRange(branchMin, branchMax), new DoubleRange(lengthMin, lengthMax),
                new DoubleRange(riseMin, riseMax), new DoubleRange(droopMin, droopMax),
                new DoubleRange(branchCurveMin, branchCurveMax), new DoubleRange(splitMin, splitMax),
                new DoubleRange(apicalMin, apicalMax), new DoubleRange(crownMin, crownMax),
                new DoubleRange(crownScaleMin, crownScaleMax), new DoubleRange(foliageMin, foliageMax),
                new IntRange(rootMin, rootMax), new DoubleRange(rootLengthMin, rootLengthMax),
                new DoubleRange(rootDropMin, rootDropMax));
    }
}
