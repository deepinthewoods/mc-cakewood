package ninja.trek.cakewood;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class CakeWoodRegistry {
    // Base CakeWood block and item
    public static final CakeWoodBlock CAKE_WOOD_BLOCK = new CakeWoodBlock(
            FabricBlockSettings.create()
                    .mapColor(MapColor.BROWN)
                    .strength(0.5f)
                    .sounds(BlockSoundGroup.WOOD)
                    .nonOpaque()
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .breakInstantly()
    );

    public static final BlockItem CAKE_WOOD_ITEM = new BlockItem(CAKE_WOOD_BLOCK,
            new Item.Settings()) {
        @Override
        public Text getName() {
            return Text.translatable("block.cakewood.cake_wood");
        }
    };

    private static final Map<String, CakeWoodBlock> CAKE_WOOD_VARIANTS = new HashMap<>();
    private static final Map<String, BlockItem> CAKE_WOOD_VARIANT_ITEMS = new HashMap<>();

    public static Map<String, CakeWoodBlock> getAllVariantBlocks() {
        return CAKE_WOOD_VARIANTS;
    }

    public static Map<String, BlockItem> getAllVariantItems() {
        return CAKE_WOOD_VARIANT_ITEMS;
    }

    public static void register() {
        // Register base CakeWood
        Registry.register(Registries.BLOCK, CakeWood.id("cake_wood"), CAKE_WOOD_BLOCK);
        Registry.register(Registries.ITEM, CakeWood.id("cake_wood"), CAKE_WOOD_ITEM);

        // Register veneered variants
        registerVariant("oak", "Oak-Veneered");
        registerVariant("spruce", "Spruce-Veneered");
        registerVariant("birch", "Birch-Veneered");
        registerVariant("jungle", "Jungle-Veneered");
        registerVariant("acacia", "Acacia-Veneered");
        registerVariant("dark_oak", "Dark Oak-Veneered");
        registerVariant("mangrove", "Mangrove-Veneered");
        registerVariant("cherry", "Cherry-Veneered");
        registerVariant("bamboo", "Bamboo-Veneered");
        registerVariant("crimson", "Crimson-Veneered");
        registerVariant("warped", "Warped-Veneered");
    }

    private static void registerVariant(String woodType, String displayName) {
        CakeWoodBlock block = new CakeWoodBlock(
                FabricBlockSettings.create()
                        .mapColor(MapColor.BROWN)
                        .strength(0.5f)
                        .sounds(BlockSoundGroup.WOOD)
                        .nonOpaque()
                        .pistonBehavior(PistonBehavior.DESTROY)
                        .breakInstantly()
        );

        final String finalDisplayName = displayName;
        BlockItem blockItem = new BlockItem(block, new Item.Settings()) {
            @Override
            public Text getName() {
                return Text.literal(finalDisplayName + " CakeWood");
            }
        };

        Registry.register(Registries.BLOCK, CakeWood.id(woodType + "_cake_wood"), block);
        Registry.register(Registries.ITEM, CakeWood.id(woodType + "_cake_wood"), blockItem);

        CAKE_WOOD_VARIANTS.put(woodType, block);
        CAKE_WOOD_VARIANT_ITEMS.put(woodType, blockItem);
    }

    public static CakeWoodBlock getVariantBlock(String woodType) {
        return CAKE_WOOD_VARIANTS.get(woodType);
    }

    public static BlockItem getVariantBlockItem(String woodType) {
        return CAKE_WOOD_VARIANT_ITEMS.get(woodType);
    }
}