
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
            generateVariantModel(generator,
                    CakeWoodRegistry.CAKE_WOOD_PLANKS_BLOCK,
                    "cake_wood_planks",
                    "cake_wood_planks_base");

            // Generate base CornerCakeWood with custom texture
            generateCornerVariantModel(generator,
                    CakeWoodRegistry.CORNER_CAKE_WOOD_BLOCK,
                    "corner_cake_wood",
                    "cake_wood");
            generateCornerVariantModel(generator,
                    CakeWoodRegistry.CORNER_CAKE_WOOD_PLANKS_BLOCK,
                    "corner_cake_wood_planks",
                    "cake_wood_planks_base");

            // Generate veneered corner variants with proper textures
            for (Map.Entry<String, CornerCakeWoodBlock> entry : CakeWoodRegistry.getAllCornerWoodVariantBlocks().entrySet()) {
                String woodType = entry.getKey();
                String textureName = (woodType.equals("crimson") || woodType.equals("warped")) ?
                        woodType + "_stem" :
                        woodType + "_log";

                // Corner CakeWood variants
                generateCornerVariantModel(generator,
                        entry.getValue(),
                        woodType + "_veneered_corner_cake_wood",
                        textureName);
            }

            // Generate plank corner variants
            for (Map.Entry<String, CornerCakeWoodBlock> entry : CakeWoodRegistry.getAllCornerPlankVariantBlocks().entrySet()) {
                String woodType = entry.getKey();
                generateCornerVariantModel(generator,
                        entry.getValue(),
                        woodType + "_veneered_corner_cake_wood_planks",
                        woodType + "_planks");
            }

            // Generate veneered variants
            for (Map.Entry<String, CakeWoodBlock> entry : CakeWoodRegistry.getAllWoodVariantBlocks().entrySet()) {
                String woodType = entry.getKey();
                String textureName = (woodType.equals("crimson") || woodType.equals("warped")) ?
                        woodType + "_stem" :
                        woodType + "_log";

                // Regular CakeWood variants
                generateVariantModel(generator,
                        entry.getValue(),
                        woodType + "_veneered_cake_wood",
                        textureName);
                generateVariantModel(generator,
                        CakeWoodRegistry.getPlankVariantBlock(woodType),
                        woodType + "_veneered_cake_wood_planks",
                        woodType + "_planks");


            }
        }

        @Override
        public void generateItemModels(ItemModelGenerator generator) {
            // Base variants
            generateItemModel(generator, CakeWoodRegistry.CAKE_WOOD_ITEM, "cake_wood");
            generateItemModel(generator, CakeWoodRegistry.CAKE_WOOD_PLANKS_ITEM, "cake_wood_planks_base");
            generateCornerItemModel(generator, CakeWoodRegistry.CORNER_CAKE_WOOD_ITEM, "cake_wood");
            generateCornerItemModel(generator, CakeWoodRegistry.CORNER_CAKE_WOOD_PLANKS_ITEM, "cake_wood_planks_base");

            // Wood variants
            for (Map.Entry<String, BlockItem> entry : CakeWoodRegistry.getAllWoodVariantItems().entrySet()) {
                String woodType = entry.getKey();
                String woodTexture = (woodType.equals("crimson") || woodType.equals("warped")) ?
                        woodType + "_stem" : woodType + "_log";

                // Regular CakeWood items
                generateItemModel(generator, entry.getValue(), woodTexture);
                generateItemModel(generator,
                        CakeWoodRegistry.getPlankVariantItem(woodType),
                        woodType + "_planks");


                // Corner CakeWood items
                generateCornerItemModel(generator,
                        CakeWoodRegistry.getCornerWoodVariantItem(woodType),
                        woodTexture);
                generateCornerItemModel(generator,
                        CakeWoodRegistry.getCornerPlankVariantItem(woodType),
                        woodType + "_planks");
            }
        }

        private void generateItemModel(ItemModelGenerator generator, BlockItem item, String textureId) {
            String textureRef = "minecraft:block/" + textureId;
            if (textureId.startsWith("cake_wood")) {
                textureRef = CakeWood.MOD_ID + ":block/" + textureId;
            }
            Model bittenModel = createBittenModel();
            bittenModel.upload(
                    ModelIds.getItemModelId(item),
                    TextureMap.all(Identifier.of(
                            textureId.startsWith("cake_wood") ? CakeWood.MOD_ID : "minecraft",
                            "block/" + textureId)),
                    generator.writer
            );
        }

        private Model createBittenModel() {
            return new Model(Optional.empty(), Optional.empty(), TextureKey.ALL) {
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
        }

        private void generateVariantModel(BlockStateModelGenerator generator,
                                          CakeWoodBlock block,
                                          String variantName,
                                          String textureId) {
            LOGGER.info("var model " + variantName);
            MultipartBlockStateSupplier stateSupplier = MultipartBlockStateSupplier.create(block);
            TextureKey WOOD_TEXTURE = TextureKey.of("wood_texture");

            for (int bites = 0; bites < CakeWoodBlock.MAX_BITES; bites++) {
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
                            float depth = 16 - biteSize;
                            int yOffset = isTop ? 8 : 0;
                            int height = 8;

                            JsonArray elements = new JsonArray();
                            JsonObject element = new JsonObject();
                            JsonArray from = new JsonArray();
                            from.add(0);
                            from.add(yOffset);
                            from.add(0);
                            element.add("from", from);

                            JsonArray to = new JsonArray();
                            to.add(16);
                            to.add(yOffset + height);
                            to.add(depth);
                            element.add("to", to);

                            JsonObject faces = new JsonObject();
                            BiFunction<Integer, Integer, JsonObject> createFace = (startV, endV) -> {
                                JsonObject face = new JsonObject();
                                face.addProperty("texture", "#wood_texture");
                                JsonArray uv = new JsonArray();
                                uv.add(0);
                                uv.add(startV);
                                uv.add(16);
                                uv.add(endV);
                                face.add("uv", uv);
                                return face;
                            };

                            faces.add("north", createFace.apply(16-height, 16));
                            faces.add("south", createFace.apply(16-height, 16));
                            faces.add("east", createFace.apply(16-height, 16));
                            faces.add("west", createFace.apply(16-height, 16));
                            faces.add("up", createFace.apply(0, (int)depth));
                            faces.add("down", createFace.apply(0, (int)depth));
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

        private void generateCornerItemModel(ItemModelGenerator generator, BlockItem item, String textureId) {
            String textureRef = "minecraft:block/" + textureId;
            if (textureId.startsWith("cake_wood")) {
                textureRef = CakeWood.MOD_ID + ":block/" + textureId;
            }
            Model cornerModel = new Model(Optional.empty(), Optional.empty(), TextureKey.ALL) {
                @Override
                public JsonObject createJson(Identifier id, Map<TextureKey, Identifier> textures) {
                    JsonObject json = new JsonObject();
                    json.addProperty("parent", "minecraft:block/block");
                    JsonObject texturesJson = new JsonObject();
                    texturesJson.addProperty("all", textures.get(TextureKey.ALL).toString());
                    texturesJson.addProperty("particle", textures.get(TextureKey.ALL).toString());
                    json.add("textures", texturesJson);

                    JsonArray elements = new JsonArray();

                    // Bottom half - full size southwest corner
                    elements.add(createCornerElement(0, 8, true));

                    // Top half - half size southwest corner (showing 4 bites taken)
                    elements.add(createCornerElement(8, 16, false));

                    json.add("elements", elements);
                    return json;
                }

                private JsonObject createCornerElement(int yMin, int yMax, boolean isFullSize) {
                    JsonObject element = new JsonObject();
                    int size = isFullSize ? 16 : 8; // 16 for bottom, 8 for bitten top

                    // For southwest corner, we want to start at x=0, but end at maxZ
                    JsonArray from = new JsonArray();
                    from.add(0);                    // x starts at 0
                    from.add(yMin);                 // y
                    from.add(16 - size);            // z starts offset from back for the given size
                    element.add("from", from);

                    JsonArray to = new JsonArray();
                    to.add(size);                   // x extends by size
                    to.add(yMax);                   // y
                    to.add(16);                     // z goes to full depth
                    element.add("to", to);

                    // Add faces with proper UV mapping
                    JsonObject faces = new JsonObject();
                    String[] faceNames = {"north", "south", "east", "west", "up", "down"};

                    for (String face : faceNames) {
                        JsonObject faceObj = new JsonObject();
                        faceObj.addProperty("texture", "#all");

                        JsonArray uv = new JsonArray();
                        switch (face) {
                            case "up", "down" -> {
                                uv.add(0);
                                uv.add(16 - size);
                                uv.add(size);
                                uv.add(16);
                            }
                            case "north", "south" -> {
                                uv.add(0);
                                uv.add(yMin);
                                uv.add(size);
                                uv.add(yMax);
                            }
                            case "east", "west" -> {
                                uv.add(16 - size);
                                uv.add(yMin);
                                uv.add(16);
                                uv.add(yMax);
                            }
                        }
                        faceObj.add("uv", uv);
                        faces.add(face, faceObj);
                    }
                    element.add("faces", faces);
                    return element;
                }
            };

            cornerModel.upload(
                    ModelIds.getItemModelId(item),
                    TextureMap.all(Identifier.of(
                            textureId.startsWith("cake_wood") ? CakeWood.MOD_ID : "minecraft",
                            "block/" + textureId)),
                    generator.writer
            );
        }

        // Update the generateCornerVariantModel method in CakeWoodDataGenerator.java

        private void generateCornerVariantModel(BlockStateModelGenerator generator,
                                                CornerCakeWoodBlock block,
                                                String variantName,
                                                String textureId) {
            LOGGER.info("corner var " + variantName);
            // Create a single MultipartBlockStateSupplier for the block
            MultipartBlockStateSupplier stateSupplier = MultipartBlockStateSupplier.create(block);
            TextureKey ALL = TextureKey.of("all");

            // Generate models for all combinations of bites and directions
            for (int bites = 0; bites < CornerCakeWoodBlock.MAX_BITES; bites++) {
                final int bitesValue = bites;
                for (boolean isTop : Arrays.asList(true, false)) {
                    for (CornerCakeWoodBlock.DiagonalDirection facing : CornerCakeWoodBlock.DiagonalDirection.values()) {
                        // Create a unique model name for each combination
                        String modelName = String.format("block/%s_%s_%d_%s",
                                variantName,
                                isTop ? "top" : "bottom",
                                bites,
                                facing.asString());

                        Model model = new Model(Optional.empty(), Optional.empty(), ALL) {
                            @Override
                            public JsonObject createJson(Identifier id, Map<TextureKey, Identifier> textures) {
                                JsonObject json = new JsonObject();
                                json.addProperty("parent", "minecraft:block/block");
                                JsonObject texturesJson = new JsonObject();
                                texturesJson.addProperty("particle", textures.get(ALL).toString());
                                texturesJson.addProperty("texture", textures.get(ALL).toString());
                                json.add("textures", texturesJson);

                                float size = 16.0f - (bitesValue * (16.0f / CornerCakeWoodBlock.MAX_BITES));
                                float yMin = isTop ? 8.0f : 0.0f;
                                float yMax = isTop ? 16.0f : 8.0f;
                                float xFrom, xTo, zFrom, zTo;

                                switch (facing) {
                                    case NORTHWEST:
                                        xFrom = 0; xTo = size; zFrom = 0; zTo = size;
                                        break;
                                    case NORTHEAST:
                                        xFrom = 16 - size; xTo = 16; zFrom = 0; zTo = size;
                                        break;
                                    case SOUTHEAST:
                                        xFrom = 16 - size; xTo = 16; zFrom = 16 - size; zTo = 16;
                                        break;
                                    case SOUTHWEST:
                                        xFrom = 0; xTo = size; zFrom = 16 - size; zTo = 16;
                                        break;
                                    default:
                                        xFrom = 0; xTo = size; zFrom = 0; zTo = size;
                                        break;
                                }

                                JsonArray elements = new JsonArray();
                                JsonObject element = new JsonObject();
                                JsonArray from = new JsonArray();
                                from.add(xFrom);
                                from.add(yMin);
                                from.add(zFrom);
                                element.add("from", from);

                                JsonArray to = new JsonArray();
                                to.add(xTo);
                                to.add(yMax);
                                to.add(zTo);
                                element.add("to", to);

                                JsonObject faces = new JsonObject();
                                for (String face : new String[]{"north", "south", "east", "west", "up", "down"}) {
                                    JsonObject faceObj = new JsonObject();
                                    faceObj.addProperty("texture", "#texture");
                                    JsonArray uv = new JsonArray();
                                    if (face.equals("up") || face.equals("down")) {
                                        uv.add(xFrom);
                                        uv.add(zFrom);
                                        uv.add(xTo);
                                        uv.add(zTo);
                                    } else {
                                        uv.add(xFrom);
                                        uv.add(yMin);
                                        uv.add(xTo);
                                        uv.add(yMax);
                                    }
                                    faceObj.add("uv", uv);
                                    faces.add(face, faceObj);
                                }
                                element.add("faces", faces);
                                elements.add(element);
                                json.add("elements", elements);
                                return json;
                            }
                        };

                        TextureMap textureMap = new TextureMap().put(ALL, Identifier.of(
                                textureId.startsWith("cake_wood") ? CakeWood.MOD_ID : "minecraft",
                                "block/" + textureId));

                        Identifier modelId = model.upload(
                                Identifier.of(CakeWood.MOD_ID, modelName),
                                textureMap,
                                generator.modelCollector
                        );

                        // Add this variant to the MultipartBlockStateSupplier
                        When condition = When.create()
                                .set(isTop ? CornerCakeWoodBlock.TOP_BITES : CornerCakeWoodBlock.BOTTOM_BITES, bitesValue)
                                .set(isTop ? CornerCakeWoodBlock.TOP_FACING : CornerCakeWoodBlock.BOTTOM_FACING, facing);

                        stateSupplier.with(condition, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, modelId));
                    }
                }
            }

            // Register the blockstate once with all variants
            generator.blockStateCollector.accept(stateSupplier);
        }

    }
}