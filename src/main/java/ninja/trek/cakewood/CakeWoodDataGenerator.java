package ninja.trek.cakewood;

// [Previous imports remain the same]

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

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
            // Generate base CakeWood variants
            generateVariantModel(generator,
                    CakeWoodRegistry.CAKE_WOOD_BLOCK,
                    "cake_wood",
                    "cake_wood");
            generateVariantModel(generator,
                    CakeWoodRegistry.CAKE_WOOD_PLANKS_BLOCK,
                    "cake_wood_planks",
                    "cake_wood_planks");

            // Generate base stripped CakeWood
            generateVariantModel(generator,
                    CakeWoodRegistry.STRIPPED_CAKE_WOOD_BLOCK,
                    "stripped_cake_wood",
                    "cake_wood_stripped");

            // Generate base CornerCakeWood variants
            generateCornerVariantModel(generator,
                    CakeWoodRegistry.CORNER_CAKE_WOOD_BLOCK,
                    "corner_cake_wood",
                    "cake_wood");
            generateCornerVariantModel(generator,
                    CakeWoodRegistry.CORNER_CAKE_WOOD_PLANKS_BLOCK,
                    "corner_cake_wood_planks",
                    "cake_wood_planks");

            // Generate base stripped corner CakeWood
            generateCornerVariantModel(generator,
                    CakeWoodRegistry.STRIPPED_CORNER_CAKE_WOOD_BLOCK,
                    "stripped_corner_cake_wood",
                    "cake_wood_stripped");

            // Generate veneered variants for each wood type
            for (Map.Entry<String, CakeWoodBlock> entry : CakeWoodRegistry.getAllWoodVariantBlocks().entrySet()) {
                String woodType = entry.getKey();
                String textureName;
                String strippedTextureName;

                if (woodType.equals("crimson") || woodType.equals("warped")) {
                    textureName = woodType + "_stem";
                    strippedTextureName = "stripped_" + woodType + "_stem";
                } else if (woodType.equals("bamboo")) {
                    textureName = "bamboo_block";
                    strippedTextureName = "stripped_bamboo_block";
                } else {
                    textureName = woodType + "_log";
                    strippedTextureName = "stripped_" + woodType + "_log";
                }

                // Regular CakeWood variants
                generateVariantModel(generator,
                        entry.getValue(),
                        woodType + "_veneered_cake_wood",
                        textureName);
                generateVariantModel(generator,
                        CakeWoodRegistry.getPlankVariantBlock(woodType),
                        woodType + "_veneered_cake_wood_planks",
                        woodType + "_planks");

                // Stripped CakeWood variants
                generateVariantModel(generator,
                        CakeWoodRegistry.getStrippedWoodVariantBlock(woodType),
                        "stripped_" + woodType + "_veneered_cake_wood",
                        strippedTextureName);

                // Corner CakeWood variants
                generateCornerVariantModel(generator,
                        CakeWoodRegistry.getCornerWoodVariantBlock(woodType),
                        woodType + "_veneered_corner_cake_wood",
                        textureName);
                generateCornerVariantModel(generator,
                        CakeWoodRegistry.getCornerPlankVariantBlock(woodType),
                        woodType + "_veneered_corner_cake_wood_planks",
                        woodType + "_planks");

                // Stripped Corner CakeWood variants
                generateCornerVariantModel(generator,
                        CakeWoodRegistry.getStrippedCornerWoodVariantBlock(woodType),
                        "stripped_" + woodType + "_veneered_corner_cake_wood",
                        strippedTextureName);
            }
        }

        @Override
        public void generateItemModels(ItemModelGenerator generator) {
            // Base variants
            generateItemModel(generator, CakeWoodRegistry.CAKE_WOOD_ITEM, "cake_wood");
            generateItemModel(generator, CakeWoodRegistry.CAKE_WOOD_PLANKS_ITEM, "cake_wood_planks");
            generateItemModel(generator, CakeWoodRegistry.STRIPPED_CAKE_WOOD_ITEM, "cake_wood_stripped");

            generateCornerItemModel(generator, CakeWoodRegistry.CORNER_CAKE_WOOD_ITEM, "cake_wood");
            generateCornerItemModel(generator, CakeWoodRegistry.CORNER_CAKE_WOOD_PLANKS_ITEM, "cake_wood_planks");
            generateCornerItemModel(generator, CakeWoodRegistry.STRIPPED_CORNER_CAKE_WOOD_ITEM, "cake_wood_stripped");

            // Wood variants
            for (Map.Entry<String, BlockItem> entry : CakeWoodRegistry.getAllWoodVariantItems().entrySet()) {
                String woodType = entry.getKey();
                String woodTexture;
                String strippedWoodTexture;

                if (woodType.equals("crimson") || woodType.equals("warped")) {
                    woodTexture = woodType + "_stem";
                    strippedWoodTexture = "stripped_" + woodType + "_stem";
                } else if (woodType.equals("bamboo")) {
                    woodTexture = "bamboo_block";
                    strippedWoodTexture = "stripped_bamboo_block";
                } else {
                    woodTexture = woodType + "_log";
                    strippedWoodTexture = "stripped_" + woodType + "_log";
                }

                // Regular CakeWood items
                generateItemModel(generator, entry.getValue(), woodTexture);
                generateItemModel(generator,
                        CakeWoodRegistry.getPlankVariantItem(woodType),
                        woodType + "_planks");

                // Stripped CakeWood items
                generateItemModel(generator,
                        CakeWoodRegistry.getStrippedWoodVariantItem(woodType),
                        strippedWoodTexture);

                // Corner CakeWood items
                generateCornerItemModel(generator,
                        CakeWoodRegistry.getCornerWoodVariantItem(woodType),
                        woodTexture);
                generateCornerItemModel(generator,
                        CakeWoodRegistry.getCornerPlankVariantItem(woodType),
                        woodType + "_planks");

                // Stripped Corner CakeWood items
                generateCornerItemModel(generator,
                        CakeWoodRegistry.getStrippedCornerWoodVariantItem(woodType),
                        strippedWoodTexture);
            }
        }

        // [Previous helper methods remain the same]



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

                            // Set up texture references
                            JsonObject texturesJson = new JsonObject();
                            String textureRef = "minecraft:block/" + textureId;
                            if (textureId.startsWith("cake_wood")) {
                                textureRef = CakeWood.MOD_ID + ":block/" + textureId;
                            }
                            texturesJson.addProperty("wood_texture", textureRef);
                            texturesJson.addProperty("particle", textureRef);
                            json.add("textures", texturesJson);

                            // Calculate dimensions
                            int biteSize = bitesValue * 2;  // each bite is 2 pixels
                            int depth = 16 - biteSize;      // remaining depth after bites
                            int yOffset = isTop ? 8 : 0;    // vertical position (top/bottom half)
                            int height = 8;                 // height of each half
                            float depthFraction = depth / 16.0f; // For UV mapping

                            // Create elements array
                            JsonArray elements = new JsonArray();
                            JsonObject element = new JsonObject();

                            // Set geometry coordinates
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

                            // Create faces with proper UV mapping
                            JsonObject faces = new JsonObject();

                            // North face (bitten face)
                            JsonObject northFace = new JsonObject();
                            northFace.addProperty("texture", "#wood_texture");
                            northFace.addProperty("cullface", "north");
                            JsonArray northUV = new JsonArray();
                            northUV.add(0);                     // u1
                            northUV.add(isTop ? 8 : 0);        // v1
                            northUV.add(16);                   // u2
                            northUV.add(isTop ? 16 : 8);      // v2
                            northFace.add("uv", northUV);
                            faces.add("north", northFace);

                            // South face
                            JsonObject southFace = new JsonObject();
                            southFace.addProperty("texture", "#wood_texture");
                            JsonArray southUV = new JsonArray();
                            southUV.add(0);                    // u1
                            southUV.add(isTop ? 8 : 0);       // v1
                            southUV.add(16);                  // u2
                            southUV.add(isTop ? 16 : 8);     // v2
                            southFace.add("uv", southUV);
                            faces.add("south", southFace);

                            // East face
                            JsonObject eastFace = new JsonObject();
                            eastFace.addProperty("texture", "#wood_texture");
                            JsonArray eastUV = new JsonArray();
                            eastUV.add(16 - depth);           // u1 (adjusted for bite)
                            eastUV.add(isTop ? 8 : 0);       // v1
                            eastUV.add(16);                  // u2
                            eastUV.add(isTop ? 16 : 8);     // v2
                            eastFace.add("uv", eastUV);
                            faces.add("east", eastFace);

                            // West face
                            JsonObject westFace = new JsonObject();
                            westFace.addProperty("texture", "#wood_texture");
                            JsonArray westUV = new JsonArray();
                            westUV.add(0);                    // u1
                            westUV.add(isTop ? 8 : 0);       // v1
                            westUV.add(depth);               // u2 (adjusted for bite)
                            westUV.add(isTop ? 16 : 8);     // v2
                            westFace.add("uv", westUV);
                            faces.add("west", westFace);

                            // Top face
                            JsonObject topFace = new JsonObject();
                            topFace.addProperty("texture", "#wood_texture");
                            if (isTop) topFace.addProperty("cullface", "up");
                            JsonArray topUV = new JsonArray();
                            topUV.add(0);                     // u1
                            topUV.add(0);                     // v1
                            topUV.add(16);                    // u2
                            topUV.add(depth);                 // v2 (adjusted for bite)
                            topFace.add("uv", topUV);
                            faces.add("up", topFace);

                            // Bottom face
                            JsonObject bottomFace = new JsonObject();
                            bottomFace.addProperty("texture", "#wood_texture");
                            if (!isTop) bottomFace.addProperty("cullface", "down");
                            JsonArray bottomUV = new JsonArray();
                            bottomUV.add(0);                   // u1
                            bottomUV.add(0);                   // v1
                            bottomUV.add(16);                  // u2
                            bottomUV.add(depth);               // v2 (adjusted for bite)
                            bottomFace.add("uv", bottomUV);
                            faces.add("down", bottomFace);

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

                    // Create variants for every horizontal facing
                    for (Direction facing : Direction.Type.HORIZONTAL) {
                        When condition = When.create()
                                .set(isTop ? CakeWoodBlock.TOP_BITES : CakeWoodBlock.BOTTOM_BITES, bitesValue)
                                .set(isTop ? CakeWoodBlock.TOP_FACING : CakeWoodBlock.BOTTOM_FACING, facing);
                        stateSupplier.with(condition, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, modelId)
                                .put(VariantSettings.Y, VariantSettings.Rotation.values()[(int) (facing.asRotation() / 90.0f)]));
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
                    int size = isFullSize ? 16 : 8;
                    JsonArray from = new JsonArray();
                    from.add(0);
                    from.add(yMin);
                    from.add(16 - size);
                    element.add("from", from);
                    JsonArray to = new JsonArray();
                    to.add(size);
                    to.add(yMax);
                    to.add(16);
                    element.add("to", to);
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

        private void generateCornerVariantModel(BlockStateModelGenerator generator,
                                                CakeWoodCornerBlock block,
                                                String variantName,
                                                String textureId) {
            // Log output (if desired)
            CakeWood.LOGGER.info("corner var " + variantName);
            MultipartBlockStateSupplier stateSupplier = MultipartBlockStateSupplier.create(block);
            TextureKey ALL = TextureKey.of("all");
            for (int bites = 0; bites < CakeWoodCornerBlock.MAX_BITES; bites++) {
                final int bitesValue = bites;
                for (boolean isTop : Arrays.asList(true, false)) {
                    for (CakeWoodCornerBlock.DiagonalDirection facing : CakeWoodCornerBlock.DiagonalDirection.values()) {
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
                                float size = 16.0f - (bitesValue * (16.0f / CakeWoodCornerBlock.MAX_BITES));
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
                        When condition = When.create()
                                .set(isTop ? CakeWoodCornerBlock.TOP_BITES : CakeWoodCornerBlock.BOTTOM_BITES, bitesValue)
                                .set(isTop ? CakeWoodCornerBlock.TOP_FACING : CakeWoodCornerBlock.BOTTOM_FACING, facing);
                        stateSupplier.with(condition, BlockStateVariant.create()
                                .put(VariantSettings.MODEL, modelId));
                    }
                }
            }
            generator.blockStateCollector.accept(stateSupplier);
        }
    }
}
