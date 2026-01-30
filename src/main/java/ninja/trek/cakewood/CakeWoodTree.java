// File: src/main/java/ninja/trek/cakewood/world/gen/tree/CakeWoodTree.java
package ninja.trek.cakewood;

import net.minecraft.block.SaplingGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;


import java.util.Optional;

// TODO [WIP]: Sapling/tree feature is incomplete.
//   - CakeWoodSaplingBlock is not registered in CakeWoodRegistry
//   - Missing placed_feature JSON (data/cakewood/worldgen/placed_feature/cake_wood_tree.json)
//   - configured_feature exists but uses spruce_leaves as placeholder foliage
//   - SaplingGenerator constructor may need updating for current MC version
//   - Tag files (saplings.json) reference unregistered cakewood:cakewood_sapling
public class CakeWoodTree {
    // Create registry keys (note we use our mod id via CakeWood.id(...))
    public static final RegistryKey<ConfiguredFeature<?, ?>> CAKE_WOOD_CONFIGURED_FEATURE_KEY =
            RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, CakeWood.id("cake_wood_tree"));
    public static final RegistryKey<PlacedFeature> CAKE_WOOD_PLACED_FEATURE_KEY =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, CakeWood.id("cake_wood_tree"));

    /**
     * Create the sapling generator. The parameters link the sapling block with the configured feature.
     * In Minecraft 1.21+, the SaplingGenerator constructor signature changed.
     * It now expects: name, regularTreeKey, megaTreeKey, flowerTreeKey
     *
     * The actual tree feature configuration should be defined in a datapack JSON file at:
     * data/cakewood/worldgen/configured_feature/cake_wood_tree.json
     */
    public static final SaplingGenerator CAKE_WOOD_SAPLING_GENERATOR =
            new SaplingGenerator(
                    "cakewood:cake_wood_tree",
                    Optional.of(CAKE_WOOD_CONFIGURED_FEATURE_KEY),
                    Optional.empty(),
                    Optional.empty()
            );

    /**
     * Note: In Minecraft 1.21+, configured and placed features are dynamic registries
     * that should be registered through data generation or datapacks, not at runtime.
     *
     * The SaplingGenerator defined above will work with the registry keys, and the
     * actual features should be defined in data/cakewood/worldgen/configured_feature/
     * and data/cakewood/worldgen/placed_feature/ JSON files.
     *
     * For more information, see the Fabric wiki on world generation:
     * https://fabricmc.net/wiki/tutorial:features
     */
    public static void register() {
        // Features are now registered through datapacks in modern Minecraft versions.
        // No runtime registration needed - the SaplingGenerator will reference the
        // registry keys which will be populated by the datapack system.
    }
}
