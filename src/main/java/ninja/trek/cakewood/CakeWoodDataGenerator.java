package ninja.trek.cakewood;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import java.util.concurrent.CompletableFuture;

public class CakeWoodDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider((output, registriesFuture) -> new CakeWoodModelGenerator(output));
        pack.addProvider((output, registriesFuture) -> new CakeWoodRecipeGenerator(output, registriesFuture));
    }

    private static class CakeWoodModelGenerator extends FabricModelProvider {
        public CakeWoodModelGenerator(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            // Register block models and states
            registerBlockModels(blockStateModelGenerator, CakeWoodRegistry.CAKE_WOOD_BLOCK,
                    Identifier.of(CakeWood.MOD_ID, "block/cake_wood"));

            // Register variants for each wood type
            CakeWoodRegistry.getAllVariantBlocks().forEach((woodType, block) -> {
                registerBlockModels(blockStateModelGenerator, block,
                        Identifier.of("minecraft", "block/" + woodType + "_planks"));
            });
        }

        private void registerBlockModels(BlockStateModelGenerator generator, CakeWoodBlock block, Identifier texture) {
            MultipartBlockStateSupplier stateSupplier = MultipartBlockStateSupplier.create(block);
            Identifier modelId = Registries.BLOCK.getId(block);

            // Generate model with textures
            Models.CUBE_ALL.upload(modelId, TextureMap.all(texture), generator.modelCollector);

            for (Direction topFacing : Direction.Type.HORIZONTAL) {
                for (Direction bottomFacing : Direction.Type.HORIZONTAL) {
                    // Calculate rotation based on direction
                    VariantSettings.Rotation rotation = switch ((int) topFacing.asRotation()) {
                        case 90 -> VariantSettings.Rotation.R90;
                        case 180 -> VariantSettings.Rotation.R180;
                        case 270 -> VariantSettings.Rotation.R270;
                        default -> VariantSettings.Rotation.R0;
                    };

                    // Create variant with rotation
                    BlockStateVariant variant = BlockStateVariant.create()
                            .put(VariantSettings.MODEL, modelId)
                            .put(VariantSettings.Y, rotation);

                    // Add variants for each bite state
                    for (int i = 0; i <= 7; i++) {
                        final int topBites = i;
                        for (int j = 0; j <= 7; j++) {
                            final int bottomBites = j;
                            stateSupplier.with(When.create()
                                            .set(CakeWoodBlock.TOP_BITES, topBites)
                                            .set(CakeWoodBlock.BOTTOM_BITES, bottomBites)
                                            .set(CakeWoodBlock.TOP_FACING, topFacing)
                                            .set(CakeWoodBlock.BOTTOM_FACING, bottomFacing),
                                    variant);
                        }
                    }
                }
            }
            generator.blockStateCollector.accept(stateSupplier);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            // Generate item model for base CakeWood
            itemModelGenerator.register(CakeWoodRegistry.CAKE_WOOD_ITEM, Models.GENERATED);

            // Generate item models for variants
            CakeWoodRegistry.getAllVariantBlocks().forEach((woodType, block) -> {
                itemModelGenerator.register(CakeWoodRegistry.getVariantBlockItem(woodType),
                        Models.GENERATED);
            });
        }
    }

    private static class CakeWoodRecipeGenerator extends FabricRecipeProvider {
        public CakeWoodRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void generate(RecipeExporter exporter) {
            // Base CakeWood recipe
            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, CakeWoodRegistry.CAKE_WOOD_BLOCK)
                    .pattern("WWW")
                    .pattern("CCC")
                    .pattern("WWW")
                    .input('W', Items.OAK_PLANKS)
                    .input('C', Items.CAKE)
                    .criterion(hasItem(Items.CAKE), conditionsFromItem(Items.CAKE))
                    .criterion(hasItem(Items.OAK_PLANKS), conditionsFromItem(Items.OAK_PLANKS))
                    .offerTo(exporter, Identifier.of(CakeWood.MOD_ID, "cake_wood"));

            // Wood variant recipes
            var woodItems = new Object[][] {
                    {"oak", Items.OAK_PLANKS},
                    {"spruce", Items.SPRUCE_PLANKS},
                    {"birch", Items.BIRCH_PLANKS},
                    {"jungle", Items.JUNGLE_PLANKS},
                    {"acacia", Items.ACACIA_PLANKS},
                    {"dark_oak", Items.DARK_OAK_PLANKS},
                    {"mangrove", Items.MANGROVE_PLANKS},
                    {"cherry", Items.CHERRY_PLANKS},
                    {"bamboo", Items.BAMBOO_PLANKS},
                    {"crimson", Items.CRIMSON_PLANKS},
                    {"warped", Items.WARPED_PLANKS}
            };

            for (Object[] wood : woodItems) {
                String woodType = (String)wood[0];
                Item planks = (Item)wood[1];
                if (CakeWoodRegistry.getVariantBlock(woodType) != null) {
                    ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS,
                                    CakeWoodRegistry.getVariantBlock(woodType))
                            .pattern("WWW")
                            .pattern("CCC")
                            .pattern("WWW")
                            .input('W', planks)
                            .input('C', CakeWoodRegistry.CAKE_WOOD_BLOCK)
                            .criterion(hasItem(CakeWoodRegistry.CAKE_WOOD_BLOCK),
                                    conditionsFromItem(CakeWoodRegistry.CAKE_WOOD_BLOCK))
                            .offerTo(exporter, Identifier.of(CakeWood.MOD_ID,
                                    woodType + "_cake_wood"));
                }
            }
        }
    }
}