// File: src/main/java/ninja/trek/cakewood/world/gen/tree/CakeWoodTree.java
package ninja.trek.cakewood;

import net.minecraft.block.SaplingGenerator;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;
import ninja.trek.cakewood.CakeWood;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;


import java.util.Optional;

public class CakeWoodTree {
    // Create registry keys (note we use our mod id via CakeWood.id(...))
    public static final RegistryKey<ConfiguredFeature<?, ?>> CAKE_WOOD_CONFIGURED_FEATURE_KEY =
            RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, CakeWood.id("cake_wood_tree"));
    public static final RegistryKey<PlacedFeature> CAKE_WOOD_PLACED_FEATURE_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, CakeWood.id("cake_wood_tree"));

    /**
     * Create a simple tree configuration.
     * (For demonstration we use vanilla oak log and leaves. In a full mod you might want to use your custom blocks.)
     */
    public static ConfiguredFeature<TreeFeatureConfig, ?> createCakeWoodTreeFeature() {
        TreeFeatureConfig config = new TreeFeatureConfig.Builder(
                // For the trunk, you might substitute your own CakeWood log block instead of OAK_LOG
                net.minecraft.world.gen.stateprovider.BlockStateProvider.of(Blocks.OAK_LOG),
                new StraightTrunkPlacer(5, 2, 2),
                // For the leaves, you can also substitute your own block if available.
                net.minecraft.world.gen.stateprovider.BlockStateProvider.of(Blocks.OAK_LEAVES),
                new BlobFoliagePlacer(2, 0),
                new TwoLayersFeatureSize(1, 0, 1)
        )
                .ignoreVines()
                .build();
        return new ConfiguredFeature<>(TreeFeature.INSTANCE, config);
    }

    /**
     * Create a placed feature.
     */
    public static PlacedFeature createCakeWoodPlacedFeature() {
        ConfiguredFeature<TreeFeatureConfig, ?> configuredFeature = createCakeWoodTreeFeature();
        return new PlacedFeature(configuredFeature, PlacedFeatures.createCountExtraModifier(0, 0.0F, 1));
    }

    /**
     * Create the sapling generator. The parameters link the sapling block (see below) with the configured feature.
     */
    public static final SaplingGenerator CAKE_WOOD_SAPLING_GENERATOR =
            new SaplingGenerator("cakewood:cake_wood_tree", Optional.empty(), Optional.of(CAKE_WOOD_CONFIGURED_FEATURE_KEY));

    /**
     * Call this method during mod initialization.
     */
    public static void register() {
        Registry.register(Registry.CONFIGURED_FEATURE, CAKE_WOOD_CONFIGURED_FEATURE_KEY.getValue(), createCakeWoodTreeFeature());
        Registry.register(Registry.PLACED_FEATURE, CAKE_WOOD_PLACED_FEATURE_KEY.getValue(), createCakeWoodPlacedFeature());
    }
}
