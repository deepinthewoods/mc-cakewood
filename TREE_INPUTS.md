# CakeWood tree inputs

CakeWood discovers sapling mappings from JSON files under `data/<namespace>/tree_inputs/`.
Datapacks may replace the built-in files, and other mods can add mappings without a Java dependency.

Each file maps one sapling block to an existing structural profile and either a weighted foliage pool:

```json
{
  "sapling": "examplemod:blue_sapling",
  "profile": "oak",
  "foliage": [
    { "block": "examplemod:blue_leaves", "weight": 3 },
    { "block": "minecraft:oak_leaves", "weight": 1 }
  ]
}
```

or a foliage tag:

```json
{
  "sapling": "examplemod:rainbow_sapling",
  "profile": "native",
  "foliage_tag": "examplemod:rainbow_leaves"
}
```

Foliage blocks must expose the ordinary leaf `persistent` and `distance` properties. Available profile IDs are
`native`, `oak`, `spruce`, `birch`, `jungle`, `acacia`, `dark_oak`, `mangrove`, `cherry`, `pale_oak`, `azalea`,
and `flowering_azalea`. Profiles control topology and numeric trait ranges; diagonal inputs affect only directional
foliage selection, while the four cardinals select the structural profile.
