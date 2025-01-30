package ninja.trek.cakewood;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public class CakeWoodRegistry {
    // Base CakeWood block and item
    public static final CakeWoodBlock CAKE_WOOD_BLOCK = new CakeWoodBlock(FabricBlockSettings.create()
            .strength(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque());
    public static final BlockItem CAKE_WOOD_ITEM = new BlockItem(CAKE_WOOD_BLOCK, new Item.Settings());

    // Wood variants
    private static final Map<String, CakeWoodBlock> CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();

    public static void register() {
        // Register base CakeWood
        Registry.register(Registries.BLOCK, CakeWood.id("cake_wood"), CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, CakeWood.id("cake_wood"), CAKE_WOOD_ITEM);

        // Register vanilla wood variants
        registerVariant("oak");
        registerVariant("spruce");
        registerVariant("birch");
        registerVariant("jungle");
        registerVariant("acacia");
        registerVariant("dark_oak");
        registerVariant("mangrove");
        registerVariant("cherry");
        registerVariant("bamboo");
        registerVariant("crimson");
        registerVariant("warped");
    }

    private static void registerVariant(String woodType) {
        CakeWoodBlock block = new CakeWoodBlock(FabricBlockSettings.create()
                .strength(0.5f)
                .sounds(BlockSoundGroup.WOOD)
                .nonOpaque());
        BlockItem blockItem = new BlockItem(block, new Item.Settings());

        Registry.register(Registries.BLOCK,
                CakeWood.id(woodType + "_cake_wood"), block);
        Registry.register(Registries.ITEM,
                CakeWood.id(woodType + "_cake_wood"), blockItem);

        CAKE_WOOD_VARIANTS.put(woodType, block);
        CAKE_WOOD_VARIANT_ITEMS.put(woodType, blockItem);
    }

    public static CakeWoodBlock getVariantBlock(String woodType) {
        return CAKE_WOOD_VARIANTS.get(woodType);
    }

    public static BlockItem getVariantBlockItem(String woodType) {
        return CAKE_WOOD_VARIANT_ITEMS.get(woodType);
    }

    public static Map<String, CakeWoodBlock> getAllVariantBlocks() {
        return CAKE_WOOD_VARIANTS;
    }
}