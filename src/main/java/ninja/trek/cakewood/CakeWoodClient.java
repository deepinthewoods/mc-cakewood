package ninja.trek.cakewood;

import net.fabricmc.api.ClientModInitializer;

public class CakeWoodClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register client-side rendering and other client-specific features here
        CakeWood.LOGGER.info("CakeWood Client Initialized");
    }
}