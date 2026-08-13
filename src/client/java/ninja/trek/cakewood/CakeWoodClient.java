package ninja.trek.cakewood;

import net.fabricmc.api.ClientModInitializer;

public class CakeWoodClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Minecraft 26.2 selects cutout rendering from each block model's
        // render_type field instead of the removed BlockRenderLayerMap API.
    }
}
