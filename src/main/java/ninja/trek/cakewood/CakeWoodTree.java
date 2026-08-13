package ninja.trek.cakewood;

import java.util.Optional;
import ninja.trek.cakewood.tree.ProceduralTreeFeature;
import ninja.trek.cakewood.tree.SplineTreePlan;
import ninja.trek.cakewood.tree.TreeInputManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Registration and prepared-plan handoff for player-grown procedural CakeWood trees. */
public final class CakeWoodTree {
    public static final ResourceKey<ConfiguredFeature<?, ?>> CAKE_WOOD_CONFIGURED_FEATURE_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, CakeWood.id("cake_wood_tree"));
    public static final TreeGrower CAKE_WOOD_SAPLING_GENERATOR = new TreeGrower(
            "cakewood:cake_wood_tree", Optional.empty(), Optional.of(CAKE_WOOD_CONFIGURED_FEATURE_KEY), Optional.empty());
    public static final Feature<NoneFeatureConfiguration> PROCEDURAL_FEATURE =
            new ProceduralTreeFeature(NoneFeatureConfiguration.CODEC);

    private static final ThreadLocal<PreparedPlan> PREPARED_PLAN = new ThreadLocal<>();

    private CakeWoodTree() {}

    public static void register() {
        Registry.register(BuiltInRegistries.FEATURE, CakeWood.id("procedural_tree"), PROCEDURAL_FEATURE);
        TreeInputManager.register();
    }

    public static void growWithPreparedPlan(BlockPos center, SplineTreePlan plan, Runnable vanillaGrowth) {
        if (PREPARED_PLAN.get() != null) throw new IllegalStateException("Nested CakeWood tree growth");
        PREPARED_PLAN.set(new PreparedPlan(center.immutable(), plan));
        try {
            vanillaGrowth.run();
        } finally {
            PREPARED_PLAN.remove();
        }
    }

    public static Optional<SplineTreePlan> takePreparedPlan(BlockPos center) {
        PreparedPlan prepared = PREPARED_PLAN.get();
        if (prepared == null || !prepared.center().equals(center)) return Optional.empty();
        PREPARED_PLAN.remove();
        return Optional.of(prepared.plan());
    }

    private record PreparedPlan(BlockPos center, SplineTreePlan plan) {}
}
