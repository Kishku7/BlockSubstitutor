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
- Optional startup scan across all loaded chunks in all dimensions
- Configurable replacement mappings
- Configurable logging
- Mod Menu + Cloth Config integration
- Safe, deterministic behavior suitable for servers and modpacks

---

## Supported Versions

This mod is built for and tested on the following versions:

| Component       | Version         |
|-----------------|-----------------|
| Minecraft       | 1.20.1          |
| Fabric Loader   | 0.14.19+        |
| Fabric API      | 0.92.0+1.20.1   |
| Cloth Config    | 11.1.106        |
| Mod Menu        | 7.2.2           |
| Java            | 17              |

**Requirements:**
- **Java 17** is required to build and run this mod
- Fabric Loader version 0.14.19 or newer recommended

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

## Building the Mod

To build the mod from source, you'll need:

- **Java 17** or newer
- **Git** (to clone the repository)

### Build Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/Kishku7/BlockSubstitutor.git
   cd BlockSubstitutor
   ```

2. Build the mod using the Gradle wrapper:
   
   **On Linux/Mac:**
   ```bash
   ./gradlew build
   ```
   
   **On Windows:**
   ```cmd
   gradlew build
   ```

3. Find the built JAR file:
   ```
   build/libs/blocksubstitutor-1.0.0.jar
   ```

The build process will compile the mod, run any tests, and package everything into a JAR file ready for use.

---

## Development Environment

### Running in Development Mode

The mod uses Fabric Loom for development. You can run the mod directly without building a JAR:

**Run Minecraft Client:**
```bash
./gradlew runClient
```

**Run Minecraft Server:**
```bash
./gradlew runServer
```

### IDE Setup

#### IntelliJ IDEA / Android Studio

1. Open IntelliJ IDEA
2. Select **File → Open**
3. Navigate to the BlockSubstitutor directory and select it
4. IntelliJ will automatically detect it as a Gradle project and import it
5. Wait for Gradle sync to complete
6. Run configurations will be automatically generated:
   - **Fabric Client** - Launches the Minecraft client
   - **Fabric Server** - Launches the Minecraft server

#### Eclipse

1. Open Eclipse
2. Select **File → Import → Gradle → Existing Gradle Project**
3. Navigate to the BlockSubstitutor directory
4. Follow the import wizard
5. After import, run configurations will be available in the Run menu

#### Visual Studio Code

1. Open VS Code
2. Install the "Extension Pack for Java" if not already installed
3. Open the BlockSubstitutor folder
4. VS Code will detect the Gradle project and configure it automatically
5. Use the Gradle tasks view to run `runClient` or `runServer`

### Gradle Tasks

Common Gradle tasks for development:

- `./gradlew build` - Build the mod
- `./gradlew clean` - Clean build artifacts
- `./gradlew runClient` - Run the Minecraft client
- `./gradlew runServer` - Run the Minecraft server
- `./gradlew genSources` - Generate Minecraft source code for reference

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