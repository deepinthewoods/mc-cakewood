# Fabric 1.21 to 1.21.10 Upgrade Guide

This document provides thorough instructions for upgrading the CakeWood mod from Minecraft 1.21 (Fabric API 0.102.0) to Minecraft 1.21.10 (Fabric API 0.135.0+).

## Table of Contents
- [Overview](#overview)
- [Version Changes Summary](#version-changes-summary)
- [Step-by-Step Upgrade Instructions](#step-by-step-upgrade-instructions)
- [Breaking Changes and Required Code Modifications](#breaking-changes-and-required-code-modifications)
- [Testing Checklist](#testing-checklist)
- [Troubleshooting](#troubleshooting)
- [Additional Resources](#additional-resources)

---

## Overview

**Current Versions:**
- Minecraft: 1.21
- Fabric Loader: 0.16.10
- Fabric API: 0.102.0+1.21
- Fabric Loom: 1.9-SNAPSHOT
- Yarn Mappings: 1.21+build.9
- Java: 21

**Target Versions:**
- Minecraft: 1.21.10
- Fabric Loader: 0.17.2
- Fabric API: 0.135.0+1.21.10 (or latest: 0.137.0+1.21.10)
- Fabric Loom: 1.11
- Yarn Mappings: 1.21.10+build.1 (or latest)
- Java: 21

**Upgrade Path:** This upgrade spans multiple Minecraft versions (1.21 → 1.21.1 → 1.21.2 → ... → 1.21.10), each with potential breaking changes.

---

## Version Changes Summary

### Critical Breaking Changes by Version

#### Minecraft 1.21.1
- **Block Entity Changes**: Mods with custom block entities must add supported blocks explicitly (minor impact for this mod - no custom block entities)

#### Minecraft 1.21.2
- **FabricBlockSettings Removed**: Replaced with vanilla `AbstractBlock.Settings` ⚠️ **AFFECTS THIS MOD**
- **Registry Keys Required**: Items and blocks require explicit `RegistryKey` during initialization ⚠️ **AFFECTS THIS MOD**
- **FabricBlockEntityType.Builder Removed**: Replaced with `FabricBlockEntityTypeBuilder` (no impact - mod doesn't use this)
- **ActionResult Consolidation**: `TypedActionResult` and `ItemActionResult` unified (low impact)
- **Entity Creation**: Requires spawn reason parameter (no impact - mod doesn't create entities)
- **Registry Method Renames**: `getEntry()` → `getOptional()`, `getOrThrow()` → `getValueOrThrow()` (no impact)

#### Minecraft 1.21.4
- **Item Color Changes**: Item coloring API updates (no impact)
- **Data Attachment Syncing**: New data attachment features (no impact)

#### Minecraft 1.21.5
- **NBT Handling Changes**: NBT system updates (no impact)
- **HUD API Updates**: HUD rendering changes (no impact)

#### Minecraft 1.21.9/1.21.10
- **Entity#getWorld Renamed**: Now `Entity#getEntityWorld` (no impact - mod doesn't use this)
- **World Render Events Removed**: Must use mixins temporarily (no impact - mod doesn't use world rendering events)
- **Resource Loader API Rework**: `ResourceManagerHelper` → `ResourceLoader` with identifiers (no impact)
- **Keybinding API Changes**: Parameters consolidated into `KeyInput` context (no impact - no keybindings)
- **Block Entity Rendering**: Now uses `OrderedRenderCommandQueue` (no impact - no block entity renderers)
- **MixinExtras**: Now bundled with Fabric Loader 0.17.0+ (version 5.0.0)

---

## Step-by-Step Upgrade Instructions

### Phase 1: Update Build Configuration

#### Step 1.1: Update `gradle.properties`

**File**: `gradle.properties`

**Current values:**
```properties
minecraft_version=1.21
yarn_mappings=1.21+build.9
loader_version=0.16.10
fabric_version=0.102.0+1.21
```

**Updated values:**
```properties
minecraft_version=1.21.10
yarn_mappings=1.21.10+build.1
loader_version=0.17.2
fabric_version=0.137.0+1.21.10
```

> **Note**: Check [Fabric Versions](https://fabricmc.net/develop/) for the latest yarn mappings and Fabric API version. As of the time of this guide, `0.137.0+1.21.10` is the latest stable release for 1.21.10.

#### Step 1.2: Update `build.gradle`

**File**: `build.gradle`

**Current value (line 2):**
```gradle
id 'fabric-loom' version '1.9-SNAPSHOT'
```

**Updated value:**
```gradle
id 'fabric-loom' version '1.11'
```

> **Why**: Loom 1.11 is required for developing mods for Minecraft 1.21.9+. It includes support for Gradle 8.10 and configuration caches.

#### Step 1.3: Update `fabric.mod.json` Dependencies

**File**: `src/main/resources/fabric.mod.json`

**Current values (lines 35-40):**
```json
"depends": {
    "fabricloader": ">=0.16.10",
    "minecraft": "~1.21",
    "java": ">=21",
    "fabric-api": "*"
}
```

**Updated values:**
```json
"depends": {
    "fabricloader": ">=0.17.2",
    "minecraft": "~1.21.10",
    "java": ">=21",
    "fabric-api": "*"
}
```

> **Explanation**:
> - `fabricloader`: Require minimum version 0.17.2 for 1.21.10 compatibility
> - `minecraft`: Use `~1.21.10` to allow patch versions (1.21.10, 1.21.11, etc.) but not minor versions

---

### Phase 2: Code Modifications

#### Step 2.1: Replace `FabricBlockSettings` with `AbstractBlock.Settings`

**⚠️ CRITICAL CHANGE**: `FabricBlockSettings` was deprecated and removed in Minecraft 1.21.2.

**File**: `src/main/java/ninja/trek/cakewood/CakeWoodRegistry.java`

**Current code (lines 3, 58-66):**
```java
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

// ...

private static FabricBlockSettings createBlockSettings() {
    return FabricBlockSettings.create()
            .mapColor(MapColor.BROWN)
            .strength(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque()
            .pistonBehavior(PistonBehavior.DESTROY)
            .breakInstantly();
}
```

**Updated code:**
```java
import net.minecraft.block.AbstractBlock;

// ...

private static AbstractBlock.Settings createBlockSettings() {
    return AbstractBlock.Settings.create()
            .mapColor(MapColor.BROWN)
            .strength(0.5f)
            .sounds(BlockSoundGroup.WOOD)
            .nonOpaque()
            .pistonBehavior(PistonBehavior.DESTROY)
            .breakInstantly();
}
```

**Changes:**
1. **Import**: Change `net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings` to `net.minecraft.block.AbstractBlock`
2. **Return type**: Change `FabricBlockSettings` to `AbstractBlock.Settings`
3. **Instance creation**: Change `FabricBlockSettings.create()` to `AbstractBlock.Settings.create()`

**Method calls**: All builder methods (`.mapColor()`, `.strength()`, etc.) remain the same - the API is identical.

#### Step 2.2: Add Registry Keys to Block and Item Registration (1.21.2+ Requirement)

**⚠️ IMPORTANT**: Starting in Minecraft 1.21.2, blocks and items require explicit `RegistryKey` during initialization. The current code may cause `NullPointerException: Block id not set` errors.

**Migration Options:**

**Option A: Use Registry Keys (Recommended for 1.21.2+)**

**File**: `src/main/java/ninja/trek/cakewood/CakeWoodRegistry.java`

Add import:
```java
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
```

**Current registration pattern (example for CAKE_WOOD):**
```java
public static final Block CAKE_WOOD_BLOCK = new CakeWoodBlock(createBlockSettings());
public static final BlockItem CAKE_WOOD_ITEM = new BlockItem(CAKE_WOOD_BLOCK, new Item.Settings());

// Later in register() method:
Registry.register(Registries.BLOCK, CakeWood.id("cake_wood"), CAKE_WOOD_BLOCK);
Registry.register(Registries.ITEM, CakeWood.id("cake_wood"), CAKE_WOOD_ITEM);
```

**Updated registration pattern with Registry Keys:**
```java
// Create registry keys
public static final RegistryKey<Block> CAKE_WOOD_BLOCK_KEY = RegistryKey.of(RegistryKeys.BLOCK, CakeWood.id("cake_wood"));
public static final RegistryKey<Item> CAKE_WOOD_ITEM_KEY = RegistryKey.of(RegistryKeys.ITEM, CakeWood.id("cake_wood"));

// Create blocks/items with registry keys
public static final Block CAKE_WOOD_BLOCK = new CakeWoodBlock(createBlockSettings().registryKey(CAKE_WOOD_BLOCK_KEY));
public static final BlockItem CAKE_WOOD_ITEM = new BlockItem(CAKE_WOOD_BLOCK, new Item.Settings().registryKey(CAKE_WOOD_ITEM_KEY).useBlockPrefixedTranslationKey());

// Register using registry keys
public static void register() {
    Registry.register(Registries.BLOCK, CAKE_WOOD_BLOCK_KEY, CAKE_WOOD_BLOCK);
    Registry.register(Registries.ITEM, CAKE_WOOD_ITEM_KEY, CAKE_WOOD_ITEM);
    // ... repeat for all blocks/items
}
```

**Option B: Simplified Pattern (If Option A causes issues)**

If the above pattern is too verbose or causes initialization issues, you can use a helper method:

```java
private static <T> RegistryKey<T> keyOf(RegistryKey<Registry<T>> registryKey, Identifier id) {
    return RegistryKey.of(registryKey, id);
}

public static void register() {
    Identifier cakeWoodId = CakeWood.id("cake_wood");
    RegistryKey<Block> blockKey = keyOf(RegistryKeys.BLOCK, cakeWoodId);
    RegistryKey<Item> itemKey = keyOf(RegistryKeys.ITEM, cakeWoodId);

    // Create with keys inline
    Block cakeWoodBlock = new CakeWoodBlock(createBlockSettings().registryKey(blockKey));
    BlockItem cakeWoodItem = new BlockItem(cakeWoodBlock, new Item.Settings().registryKey(itemKey));

    Registry.register(Registries.BLOCK, blockKey, cakeWoodBlock);
    Registry.register(Registries.ITEM, itemKey, cakeWoodItem);
}
```

**⚠️ COMPATIBILITY NOTE**: Based on the official migration guide, registry keys became mandatory in 1.21.2. However, some sources suggest they may still be optional in certain contexts. If you encounter issues after upgrading:
1. **Test without registry keys first** - if the mod works, you may defer this change
2. **If you see `NullPointerException: Block id not set`**, then implement registry keys
3. **For maximum compatibility** with future versions, implement registry keys now

#### Step 2.3: Update Yarn Mappings References (If Applicable)

Some method names may have changed in Yarn mappings between 1.21 and 1.21.10. Based on the analysis:

**No breaking mapping changes detected in this codebase**, but be aware of:
- `Entity#getWorld()` → `Entity#getEntityWorld()` (not used in this mod)
- `Registry#getEntry()` → `Registry#getOptional()` (not used in this mod)
- `Registry#getOrThrow()` → `Registry#getValueOrThrow()` (not used in this mod)

---

### Phase 3: Gradle Refresh and Build

#### Step 3.1: Refresh Gradle Dependencies

After updating all configuration files, refresh Gradle to download new dependencies:

```bash
./gradlew --refresh-dependencies
```

Or in your IDE:
- **IntelliJ IDEA**: Right-click project → Gradle → Reload Gradle Project
- **Eclipse**: Right-click project → Gradle → Refresh Gradle Project
- **VS Code**: Run "Java: Clean Java Language Server Workspace" command

#### Step 3.2: Regenerate Run Configurations

Loom 1.11 may have updated run configuration templates:

```bash
./gradlew genSources
```

#### Step 3.3: Build the Mod

Test the build to ensure all changes compile:

```bash
./gradlew build
```

**Expected output:**
```
BUILD SUCCESSFUL in Xs
```

**If build fails:**
- Review error messages for incompatible API usage
- Check [Troubleshooting](#troubleshooting) section below
- Ensure all imports are updated (no references to `FabricBlockSettings`)

---

### Phase 4: Data Generation

The mod uses Fabric Data Generation for models and blockstates. Verify data generation still works:

```bash
./gradlew runDatagen
```

**Verify generated files in:**
- `src/generated/resources/assets/cakewood/models/`
- `src/generated/resources/assets/cakewood/blockstates/`

**Expected behavior**: No errors, all models and blockstates regenerated successfully.

---

## Testing Checklist

After completing the upgrade, test the following functionality:

### Build and Startup Tests
- [ ] `./gradlew build` completes without errors
- [ ] `./gradlew runDatagen` completes without errors
- [ ] Mod loads in Minecraft 1.21.10 without crashes
- [ ] No warnings in console about deprecated API usage
- [ ] Mod appears in Mods menu with correct version number

### Functional Tests (In-Game)
- [ ] **Block Placement**: All cake wood blocks can be placed
  - Regular cake wood blocks (all variants)
  - Planks (all variants)
  - Stripped variants
  - Corner blocks (all variants)
- [ ] **Block Interaction**: Eating mechanics work correctly
  - Right-click to eat cake wood blocks
  - Bite counter decrements properly (TOP_BITES, BOTTOM_BITES)
  - Blocks break when fully consumed
  - Correct sounds play when eating
- [ ] **Waxing Mechanics**: Blocks can be waxed
  - Use honeycomb to wax blocks
  - WAXED property applied correctly
  - Waxed blocks prevent further eating
- [ ] **Stripping Mechanics**: Logs can be stripped
  - Use axe to strip cake wood logs
  - Stripped variants placed correctly
  - StrippableBlockRegistry integration works
- [ ] **Corner Blocks**: Directional placement works
  - Corner blocks face correct diagonal direction
  - DIAGONAL property (NORTHWEST, NORTHEAST, etc.) applied correctly
  - Rotation based on player position
- [ ] **Collision/Hitboxes**: VoxelShape calculations correct
  - Blocks have correct hitboxes based on bite state
  - Collision detection works properly
  - Visual outline matches collision box
- [ ] **Rendering**: All textures and models display correctly
  - Block states render properly
  - Item models in inventory/hand correct
  - Multipart blockstates work (for complex shapes)
- [ ] **Translation Keys**: All text displays correctly
  - Block names in creative menu
  - Item tooltips (if any)
  - No "missingno" texture errors

### Compatibility Tests
- [ ] Compatible with other Fabric mods (test with popular mods if possible)
- [ ] No conflicts with Fabric API modules
- [ ] Works in both singleplayer and multiplayer
- [ ] Data packs load correctly (if applicable)

---

## Troubleshooting

### Common Issues and Solutions

#### Issue: `Cannot resolve symbol 'FabricBlockSettings'`
**Cause**: Import not updated to use vanilla `AbstractBlock.Settings`

**Solution**: Replace all imports and references:
```java
// Old
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
FabricBlockSettings settings = FabricBlockSettings.create();

// New
import net.minecraft.block.AbstractBlock;
AbstractBlock.Settings settings = AbstractBlock.Settings.create();
```

---

#### Issue: `NullPointerException: Block id not set` or `Item id not set`
**Cause**: Registry keys not provided to Block/Item settings (required in 1.21.2+)

**Solution**: Add `.registryKey()` to settings before creating blocks/items:
```java
Identifier id = CakeWood.id("cake_wood");
RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
Block block = new CakeWoodBlock(createBlockSettings().registryKey(key));
```

---

#### Issue: Build fails with "Unsupported class file major version"
**Cause**: Gradle JVM or Java version mismatch

**Solution**: Ensure using Java 21:
```bash
./gradlew build -Dorg.gradle.java.home=/path/to/jdk-21
```

Or configure in `gradle.properties`:
```properties
org.gradle.java.home=/path/to/jdk-21
```

---

#### Issue: Mod crashes on launch with `MixinException`
**Cause**: Mixin target class changed or Mixin compatibility level incorrect

**Solution**:
1. Check mixin configurations (`cakewood.mixins.json`, `cakewood.client.mixins.json`)
2. Verify compatibility level is `JAVA_21` (should already be set)
3. If using custom mixins, review target classes for signature changes
4. Check Fabric Loader includes MixinExtras 5.0.0 (bundled with 0.17.0+)

---

#### Issue: "Entity#getWorld cannot be resolved"
**Cause**: Method renamed to `getEntityWorld` in 1.21.9+

**Solution**: Replace all instances:
```java
// Old
Entity entity = ...;
World world = entity.getWorld();

// New
Entity entity = ...;
World world = entity.getEntityWorld();
```

*Note: This mod doesn't currently use this method, so this shouldn't affect you.*

---

#### Issue: Data generation fails
**Cause**: Model generator API changes or file paths incorrect

**Solution**:
1. Verify `CakeWoodDataGenerator` implements `DataGeneratorEntrypoint`
2. Check `CakeWoodModelGenerator` extends `FabricModelProvider`
3. Ensure all block/item references are valid
4. Delete `src/generated/` folder and regenerate: `./gradlew runDatagen`

---

#### Issue: IntelliJ/IDE doesn't recognize new dependencies
**Cause**: IDE cache not refreshed after Gradle update

**Solution**:
1. File → Invalidate Caches → Invalidate and Restart
2. Or: Right-click project → Gradle → Reload Gradle Project
3. Or: Delete `.gradle/` and `.idea/` folders, reimport project

---

## Breaking Changes Summary Table

| Component | Old (1.21) | New (1.21.10) | Impact | Required Action |
|-----------|-----------|---------------|--------|-----------------|
| **Minecraft** | 1.21 | 1.21.10 | High | Update gradle.properties |
| **Fabric Loader** | 0.16.10 | 0.17.2 | Medium | Update gradle.properties, fabric.mod.json |
| **Fabric API** | 0.102.0+1.21 | 0.135.0+1.21.10 | Medium | Update gradle.properties |
| **Fabric Loom** | 1.9-SNAPSHOT | 1.11 | Low | Update build.gradle |
| **FabricBlockSettings** | Available | Removed | **High** | Replace with `AbstractBlock.Settings` |
| **Registry Keys** | Optional | Required* | **High** | Add `.registryKey()` to settings |
| **Entity#getWorld** | Available | Renamed | Low | Not used in this mod |
| **ResourceManagerHelper** | Available | Deprecated | Low | Not used in this mod |
| **World Render Events** | Available | Removed | Low | Not used in this mod |

*Registry keys are mandatory in 1.21.2+. Test your mod - if crashes occur with "Block/Item id not set", implement registry keys.

---

## Migration Checklist

Use this checklist to track your upgrade progress:

### Configuration Files
- [ ] Update `gradle.properties` → Minecraft version to 1.21.10
- [ ] Update `gradle.properties` → Fabric Loader to 0.17.2
- [ ] Update `gradle.properties` → Fabric API to 0.137.0+1.21.10
- [ ] Update `gradle.properties` → Yarn mappings to 1.21.10+build.1 (or latest)
- [ ] Update `build.gradle` → Fabric Loom to 1.11
- [ ] Update `fabric.mod.json` → fabricloader dependency to >=0.17.2
- [ ] Update `fabric.mod.json` → minecraft dependency to ~1.21.10

### Code Changes
- [ ] Replace `FabricBlockSettings` import with `AbstractBlock`
- [ ] Replace `FabricBlockSettings.create()` with `AbstractBlock.Settings.create()`
- [ ] Update method return types from `FabricBlockSettings` to `AbstractBlock.Settings`
- [ ] Add registry keys to block registrations (if needed)
- [ ] Add registry keys to item registrations (if needed)
- [ ] Review and update any deprecated API usage

### Build and Test
- [ ] Run `./gradlew --refresh-dependencies`
- [ ] Run `./gradlew genSources`
- [ ] Run `./gradlew build` → Success
- [ ] Run `./gradlew runDatagen` → Success
- [ ] Test mod in Minecraft 1.21.10 client → Loads successfully
- [ ] Verify all blocks place and render correctly
- [ ] Verify all gameplay mechanics work (eating, waxing, stripping)
- [ ] Test in multiplayer/server environment

---

## Additional Resources

### Official Documentation
- **Fabric Website**: https://fabricmc.net/
- **Fabric Wiki**: https://fabricmc.net/wiki/
- **Version-Specific Guides**:
  - [Fabric for Minecraft 1.21](https://fabricmc.net/2024/05/31/121.html)
  - [Fabric for Minecraft 1.21.2](https://fabricmc.net/2024/10/14/1212.html) - FabricBlockSettings removal
  - [Fabric for Minecraft 1.21.9 & 1.21.10](https://fabricmc.net/2025/09/23/1219.html) - Latest changes

### Dependency Downloads
- **Fabric Loader Releases**: https://github.com/FabricMC/fabric-loader/releases
- **Fabric API Releases**: https://modrinth.com/mod/fabric-api/versions
- **Yarn Mappings**: https://fabricmc.net/develop/

### Community Support
- **Fabric Discord**: https://discord.gg/v6v4pMv (Channel: #mod-dev)
- **Fabric GitHub**: https://github.com/FabricMC/fabric
- **Fabric Forum**: https://fabricmc.net/discuss/

### API Documentation
- **Fabric API Javadocs**: https://maven.fabricmc.net/docs/fabric-api-0.137.0+1.21.10/
- **Yarn Mappings Search**: https://fabricmc.net/develop/

---

## Version History

| Date | Minecraft Version | Fabric API Version | Notes |
|------|-------------------|-------------------|-------|
| Initial | 1.21 | 0.102.0+1.21 | Original mod version |
| Target | 1.21.10 | 0.137.0+1.21.10 | This upgrade guide |

---

## Notes

- **Backward Compatibility**: After upgrading to 1.21.10, the mod will NOT be compatible with 1.21 or earlier versions
- **Forward Compatibility**: Future versions (1.21.11+) may require additional updates
- **Experimental Features**: Minecraft 1.21.10 may include experimental features - test thoroughly
- **Mappings Updates**: Yarn mappings may receive updates; check for the latest build number
- **Fabric API Updates**: Fabric API receives frequent updates; 0.137.0 may not be the latest when you read this

**Recommended Approach**: Complete all configuration updates first, then code changes, then test. This isolates issues to specific phases.

---

**Last Updated**: 2025-11-04
**Guide Version**: 1.0
**For Mod**: CakeWood (ninja.trek.cakewood)
