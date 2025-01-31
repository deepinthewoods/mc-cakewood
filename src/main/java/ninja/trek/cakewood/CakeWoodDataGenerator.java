
package ninja.trek.cakewood;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import java.util.*;
import java.util.function.BiFunction;

import static ninja.trek.cakewood.CakeWoodBlock.MAX_BITES;

public class CakeWoodDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(CakeWoodModelGenerator::new);
    }
    private static class CakeWoodModelGenerator extends FabricModelProvider {
        public CakeWoodModelGenerator(FabricDataOutput output) {
            super(output);
        }
        @Override
        public void generateBlockStateModels(BlockStateModelGenerator generator) {
            // Generate base CakeWood with custom texture
            generateVariantModel(generator,
                    CakeWoodRegistry.CAKE_WOOD_BLOCK,
                    "cake_wood",
                    "cake_wood");
            // Generate base CakeWood Planks with custom texture
            generateVariantModel(generator,
                    CakeWoodRegistry.CAKE_WOOD_PLANKS_BLOCK,
                    "cake_wood_planks",
                    "cake_wood_planks_base");
            // Generate veneered variants
            for (Map.Entry<String, CakeWoodBlock> entry : CakeWoodRegistry.getAllWoodVariantBlocks().entrySet()) {
                String woodType = entry.getKey();
                String textureName = (woodType.equals("crimson") || woodType.equals("warped")) ?
                        woodType + "_stem" :
                        woodType + "_log";
                // Wood variant - use log/stem texture for all sides
                generateVariantModel(generator,
                        entry.getValue(),
                        woodType + "_veneered_cake_wood",
                        textureName);
                // Plank variant
                CakeWoodBlock plankBlock = CakeWoodRegistry.getPlankVariantBlock(woodType);
                generateVariantModel(generator,
                        plankBlock,
                        woodType + "_veneered_cake_wood_planks",
                        woodType + "_planks");
            }
        }
        @Override
        public void generateItemModels(ItemModelGenerator generator) {
            // Custom model for items showing one bite
            Model bittenModel = new Model(Optional.empty(), Optional.empty(), TextureKey.ALL) {
                @Override
                public JsonObject createJson(Identifier id, Map<TextureKey, Identifier> textures) {
                    JsonObject json = new JsonObject();
                    json.addProperty("parent", "minecraft:block/block");
                    JsonObject texturesJson = new JsonObject();
                    texturesJson.addProperty("all", textures.get(TextureKey.ALL).toString());
                    texturesJson.addProperty("particle", textures.get(TextureKey.ALL).toString());
                    json.add("textures", texturesJson);
                    JsonArray elements = new JsonArray();
                    // Top half - with bite on south side
                    elements.add(createCuboid(1, 8, 1, 15, 16, 13));
                    // Bottom half - with one bite
                    elements.add(createCuboid(1, 0, 3, 15, 8, 15));
                    json.add("elements", elements);
                    return json;
                }
                private JsonObject createCuboid(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
                    JsonObject element = new JsonObject();
                    JsonArray from = new JsonArray();
                    from.add(fromX);
                    from.add(fromY);
                    from.add(fromZ);
                    element.add("from", from);
                    JsonArray to = new JsonArray();
                    to.add(toX);
                    to.add(toY);
                    to.add(toZ);
                    element.add("to", to);
                    JsonObject faces = new JsonObject();
                    for (String face : new String[]{"north", "south", "east", "west", "up", "down"}) {
                        JsonObject faceObj = new JsonObject();
                        faceObj.addProperty("texture", "#all");
                        faces.add(face, faceObj);
                    }
                    element.add("faces", faces);
                    return element;
                }
            };
            // Generate models for all variants using the bitten model
            bittenModel.upload(
                    ModelIds.getItemModelId(CakeWoodRegistry.CAKE_WOOD_ITEM),
                    TextureMap.all(Identifier.of(CakeWood.MOD_ID, "block/cake_wood")),
                    generator.writer
            );
            bittenModel.upload(
                    ModelIds.getItemModelId(CakeWoodRegistry.CAKE_WOOD_PLANKS_ITEM),
                    TextureMap.all(Identifier.of(CakeWood.MOD_ID, "block/cake_wood_planks_base")),
                    generator.writer
            );
            for (Map.Entry<String, BlockItem> entry : CakeWoodRegistry.getAllWoodVariantItems().entrySet()) {
                String woodType = entry.getKey();
                String woodTexture = (woodType.equals("crimson") || woodType.equals("warped")) ?
                        woodType + "_stem" : woodType + "_log";
                bittenModel.upload(
                        ModelIds.getItemModelId(entry.getValue()),
                        TextureMap.all(Identifier.of("minecraft", "block/" + woodTexture)),
                        generator.writer
                );
                bittenModel.upload(
                        ModelIds.getItemModelId(CakeWoodRegistry.getPlankVariantItem(woodType)),
                        TextureMap.all(Identifier.of("minecraft", "block/" + woodType + "_planks")),
                        generator.writer
                );
            }
        }
        private void generateItemModel(ItemModelGenerator generator, Model model, BlockItem item, Identifier texture) {
            model.upload(
                    ModelIds.getItemModelId(item),
                    new TextureMap().put(TextureKey.of("wood_texture"), texture),
                    generator.writer
            );
        }
        private void generateVariantModel(BlockStateModelGenerator generator,
                                          CakeWoodBlock block,
                                          String variantName,
                                          String textureId) {
            MultipartBlockStateSupplier stateSupplier = MultipartBlockStateSupplier.create(block);
            TextureKey WOOD_TEXTURE = TextureKey.of("wood_texture");
            for (int bites = 0; bites <= MAX_BITES; bites++) {
                final int bitesValue = bites;
                for (boolean isTop : Arrays.asList(true, false)) {
                    String modelName = String.format("block/%s_%s_%d",
                            variantName,
                            isTop ? "top" : "bottom",
                            bites);
                    Model model = new Model(
                            Optional.of(Identifier.of(CakeWood.MOD_ID, "block/cake_wood_template")),
                            Optional.empty(),
                            WOOD_TEXTURE
                    ) {
                        @Override
                        public JsonObject createJson(Identifier id, Map<TextureKey, Identifier> textures) {
                            JsonObject json = new JsonObject();
                            json.addProperty("parent", "minecraft:block/block");
                            JsonObject texturesJson = new JsonObject();
                            String textureRef = "minecraft:block/" + textureId;
                            if (textureId.startsWith("cake_wood")) {
                                textureRef = CakeWood.MOD_ID + ":block/" + textureId;
                            }
                            texturesJson.addProperty("wood_texture", textureRef);
                            texturesJson.addProperty("particle", textureRef);
                            json.add("textures", texturesJson);
                            float biteSize = bitesValue * (16.0f / CakeWoodBlock.MAX_BITES);
                            float depth = 16 - biteSize;  // Changed from 14 to 16
                            int yOffset = isTop ? 8 : 0;
                            int height = 8;
                            JsonArray elements = new JsonArray();
                            JsonObject element = new JsonObject();
                            JsonArray from = new JsonArray();
                            from.add(0);  // Changed from 1
                            from.add(yOffset);
                            from.add(0);  // Changed from 1
                            element.add("from", from);
                            JsonArray to = new JsonArray();
                            to.add(16);  // Changed from 15
                            to.add(yOffset + height);
                            to.add(depth);  // Now goes up to 16 - biteSize
                            element.add("to", to);
                            JsonObject faces = new JsonObject();
                            BiFunction<Integer, Integer, JsonObject> createFace = (startV, endV) -> {
                                JsonObject face = new JsonObject();
                                face.addProperty("texture", "#wood_texture");
                                JsonArray uv = new JsonArray();
                                uv.add(0);   // Changed from 1
                                uv.add(startV);
                                uv.add(16);  // Changed from 15
                                uv.add(endV);
                                face.add("uv", uv);
                                return face;
                            };
                            faces.add("north", createFace.apply(16-height, 16));
                            faces.add("south", createFace.apply(16-height, 16));
                            faces.add("east", createFace.apply(16-height, 16));
                            faces.add("west", createFace.apply(16-height, 16));
                            faces.add("up", createFace.apply(0, (int)depth));    // Changed from 1
                            faces.add("down", createFace.apply(0, (int)depth));  // Changed from 1
                            element.add("faces", faces);
                            elements.add(element);
                            json.add("elements", elements);
                            return json;
                        }
                    };
                    TextureMap textureMap = new TextureMap()
                            .put(WOOD_TEXTURE, Identifier.of(
                                    textureId.startsWith("cake_wood") ? CakeWood.MOD_ID : "minecraft",
                                    "block/" + textureId));
                    Identifier modelId = model.upload(
                            Identifier.of(CakeWood.MOD_ID, modelName),
                            textureMap,
                            generator.modelCollector
                    );
                    for (Direction facing : Direction.Type.HORIZONTAL) {
                        When condition = When.create()
                                .set(isTop ? CakeWoodBlock.TOP_BITES : CakeWoodBlock.BOTTOM_BITES, bitesValue)
                                .set(isTop ? CakeWoodBlock.TOP_FACING : CakeWoodBlock.BOTTOM_FACING, facing);
                        stateSupplier.with(condition, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, modelId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.values()
                                        [(int) (facing.asRotation() / 90.0f)])
                        );
                    }
                }
            }
            generator.blockStateCollector.accept(stateSupplier);
        }
    }
}