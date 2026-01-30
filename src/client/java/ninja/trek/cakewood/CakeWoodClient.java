package ninja.trek.cakewood;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;

public class CakeWoodClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// All CakeWood blocks use nonOpaque() and have complex bite-cutout shapes,
		// so they need cutout render layer to avoid adjacent face rendering artifacts.
		BlockRenderLayer cutout = BlockRenderLayer.CUTOUT;

		// Base blocks
		BlockRenderLayerMap.putBlock(CakeWoodRegistry.CAKE_WOOD_BLOCK, cutout);
		BlockRenderLayerMap.putBlock(CakeWoodRegistry.CAKE_WOOD_PLANKS_BLOCK, cutout);
		BlockRenderLayerMap.putBlock(CakeWoodRegistry.STRIPPED_CAKE_WOOD_BLOCK, cutout);
		BlockRenderLayerMap.putBlock(CakeWoodRegistry.CORNER_CAKE_WOOD_BLOCK, cutout);
		BlockRenderLayerMap.putBlock(CakeWoodRegistry.CORNER_CAKE_WOOD_PLANKS_BLOCK, cutout);
		BlockRenderLayerMap.putBlock(CakeWoodRegistry.STRIPPED_CORNER_CAKE_WOOD_BLOCK, cutout);

		// Veneered variants
		for (CakeWoodBlock block : CakeWoodRegistry.getAllWoodVariantBlocks().values()) {
			BlockRenderLayerMap.putBlock(block, cutout);
		}
		for (CakeWoodBlock block : CakeWoodRegistry.getAllPlankVariantBlocks().values()) {
			BlockRenderLayerMap.putBlock(block, cutout);
		}
		for (CakeWoodBlock block : CakeWoodRegistry.getAllStrippedWoodVariantBlocks().values()) {
			BlockRenderLayerMap.putBlock(block, cutout);
		}

		// Corner variants
		for (CakeWoodCornerBlock block : CakeWoodRegistry.getAllCornerWoodVariantBlocks().values()) {
			BlockRenderLayerMap.putBlock(block, cutout);
		}
		for (CakeWoodCornerBlock block : CakeWoodRegistry.getAllCornerPlankVariantBlocks().values()) {
			BlockRenderLayerMap.putBlock(block, cutout);
		}
		for (CakeWoodCornerBlock block : CakeWoodRegistry.getAllStrippedCornerWoodVariantBlocks().values()) {
			BlockRenderLayerMap.putBlock(block, cutout);
		}
	}
}
