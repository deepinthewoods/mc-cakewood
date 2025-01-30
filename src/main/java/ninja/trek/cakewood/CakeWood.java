package ninja.trek.cakewood;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CakeWood implements ModInitializer {
    public static final String MOD_ID = "cakewood";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Register all CakeWood variants
        CakeWoodRegistry.register();

        LOGGER.info("CakeWood Mod Initialized - Let them eat cake... wood!");
    }

    // Utility method to create identifiers for this mod
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}