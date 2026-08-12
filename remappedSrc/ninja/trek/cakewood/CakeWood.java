package ninja.trek.cakewood;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CakeWood implements ModInitializer {
    public static final String MOD_ID = "cakewood";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Register all CakeWood variants
        CakeWoodRegistry.register();

        // TODO: Register sapling block and call CakeWoodTree.register() once sapling/tree WIP is complete.
        //   - Add sapling block + item registration in CakeWoodRegistry
        //   - Create placed_feature JSON for cake_wood_tree (configured_feature exists but placed_feature is missing)
        //   - Verify SaplingGenerator constructor matches current MC API

        LOGGER.info("CakeWood Mod Initialized - Let them eat cake... wood!");
    }

    // Utility method to create identifiers for this mod
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}