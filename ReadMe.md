# Block Substitutor

Block Substitutor is a lightweight, server-friendly downgrade compatibility mod for Minecraft 1.20.1.
It automatically replaces unsupported 1.21.x blocks with safe 1.20.1 equivalents during world load,
chunk load, and optional startup scanning.

This mod is designed for players and servers who want to use worlds generated in newer versions of
Minecraft while staying on 1.20.1 for modpack stability or performance reasons.

Author: **Kishku7**  
Wiki: https://github.com/Kishku7/BlockSubstitutor/wiki  
Source: https://github.com/Kishku7/BlockSubstitutor

---

## Features

- Automatic block substitution for unsupported 1.21.x blocks
- Full blockstate property preservation (where compatible)
- Block entity NBT preservation
- Chunk-load scanning (always enabled)
- Configurable replacement mappings
- Configurable logging
- Mod Menu + Cloth Config integration
- Safe, deterministic behavior suitable for servers and modpacks

---

## How It Works

Block Substitutor intercepts block loads and replaces blocks that do not exist in 1.20.1 with
visually and functionally appropriate alternatives.

Examples:

- `minecraft:crafter` -> `minecraft:crafting_table`
- `minecraft:copper_door` -> `minecraft:iron_door`
- `minecraft:trial_spawner` -> `minecraft:spawner`
- `minecraft:pale_oak_planks` -> `minecraft:birch_planks`
- `minecraft:resin_bricks` -> `minecraft:red_nether_bricks`

Mappings are fully configurable, change them however you like, but do so BEFORE loading the world into 1.20.1, once the world loads, things start to change. you can't try again unless you are working on a backup!

---

## Installation

1. Install Fabric Loader 0.14.21 or newer  
2. Install Fabric API  
3. Install Cloth Config API  
4. Install Mod Menu (optional but recommended)  
5. Place `BlockSubstitutor-x.x.x.jar` into your `mods` folder

Works on:

- Singleplayer
- Multiplayer servers
- Integrated servers
- Modpacks

---

## Configuration

Block Substitutor uses a JSON config file:

    config/blocksubstitutor.json

A defaults file is bundled with the mod:

    blocksubstitutor-defaults.json

### Config Options

| Option                   | Type    | Default | Description                                                        |
|--------------------------|---------|---------|--------------------------------------------------------------------|
| `scanOverworldOnStartup` | boolean | false   | Scans all loaded chunks in all dimensions when the server starts. |
| `logReplacements`        | boolean | true    | Logs a summary of replacements per chunk.                          |
| `blockMappings`          | object  | varies  | Mapping of source block IDs to replacement block IDs.             |

### Editing the Config

#### 1. In-game (recommended)

Requires Mod Menu + Cloth Config.

Path:

    Mods -> Block Substitutor -> Configure

#### 2. Manual editing

Open:

    config/blocksubstitutor.json

Mappings are simple key-value pairs:

    "minecraft:crafter": "minecraft:crafting_table"

---

## Replacement Philosophy

Mappings are chosen based on:

- Visual similarity
- Functional similarity
- Survival-friendly behavior
- Avoiding world corruption
- Avoiding block entity mismatches
- Avoiding crashes caused by missing properties

Every mapping is intentionally selected and can be overridden by the user.

---

## Logging

When `logReplacements` is enabled, the mod prints a summary like:

    [BlockSubstitutor] Replacement summary for chunk 12, -4 in dimension minecraft:overworld:
      minecraft:crafter -> minecraft:crafting_table : 3
      minecraft:copper_door -> minecraft:iron_door : 1

Missing blockstate properties are logged once per block pair to avoid spam.

---

## Compatibility

- Fully compatible with Fabric 1.20.1
- Works with most world-editing tools
- Safe for servers and modpacks
- Does not modify or add blocks
- Can run server-side without requiring clients to install the mod

---

## License

This project is licensed under the Apache License, Version 2.0.

You may use, modify, and distribute this software in compliance with the terms of the Apache 2.0 license.

Full license text:
    https://www.apache.org/licenses/LICENSE-2.0

---

## Contributing

Pull requests and mapping improvements are welcome.
Please open an issue on GitHub if you find a missing or incorrect mapping.

---

## Support

For documentation, examples, and advanced usage, visit the wiki:

https://github.com/Kishku7/BlockSubstitutor/wiki