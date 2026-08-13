package ninja.trek.cakewood;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CakeWood implements ModInitializer {
    public static final String MOD_ID = "cakewood";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Register all CakeWood variants
        CakeWoodRegistry.register();
        CakeWoodTree.register();
        CreativeModeTabEvents.MODIFY_OUTPUT_ALL.register((group, entries) -> {
            if (group == CreativeModeTabs.getDefaultTab()) entries.accept(CakeWoodRegistry.CAKE_WOOD_SAPLING_ITEM);
        });

        LOGGER.info("CakeWood Mod Initialized - Let them eat cake... wood!");
    }

    // Utility method to create identifiers for this mod
    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
