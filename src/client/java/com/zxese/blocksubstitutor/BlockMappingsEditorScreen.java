package com.zxese.blocksubstitutor;

import com.zxese.blocksubstitutor.config.BlockSubConfig;
import com.zxese.blocksubstitutor.config.ConfigManager;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.HashMap;

/**
 * Custom screen that provides a large multi-line editor for block mappings.
 *
 * Lines are in the form:
 *   source = replacement
 * Bare ids default to the minecraft: namespace.
 */
public class BlockMappingsEditorScreen extends Screen {

    private final Screen parent;
    private final BlockSubConfig config;

    private TextFieldWidget textField;

    public BlockMappingsEditorScreen(Screen parent, BlockSubConfig config) {
        super(Text.literal("Block Substitutor Mappings"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        super.init();

        int padding = 10;
        int buttonHeight = 20;
        int buttonWidth = 100;

        // Create a large text field taking most of the screen.
        int textX = padding;
        int textY = padding + 20; // leave room for title
        int textWidth = this.width - padding * 2;
        int textHeight = this.height - textY - padding - buttonHeight - 10;

        textField = new TextFieldWidget(this.textRenderer, textX, textY, textWidth, textHeight, Text.literal("Mappings"));
        textField.setText(serializeMappingsToText(config));
        textField.setMaxLength(Integer.MAX_VALUE); // effectively unlimited

        this.addDrawableChild(textField);

        // Save button
        int saveX = this.width / 2 - buttonWidth - 5;
        int saveY = this.height - padding - buttonHeight;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save"), button -> {
            HashMap<String, String> newMappings = parseMappingsFromText(textField.getText());
            config.setBlockMappings(newMappings);
            ConfigManager.save(FabricLoader.getInstance().getConfigDir());
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(saveX, saveY, buttonWidth, buttonHeight).build());

        // Cancel button
        int cancelX = this.width / 2 + 5;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), button -> {
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(cancelX, saveY, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    // ---------------------------------------------------------
    // Text <-> Map helpers (duplicated from BlockSubstitutorConfigScreen for now)
    // ---------------------------------------------------------

    private static String serializeMappingsToText(BlockSubConfig config) {
        StringBuilder builder = new StringBuilder();

        config.getBlockMappings().forEach((source, replacement) -> {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(source)
                    .append(" = ")
                    .append(replacement);
        });

        return builder.toString();
    }

    private static HashMap<String, String> parseMappingsFromText(String text) {
        HashMap<String, String> result = new HashMap<>();

        if (text == null || text.isEmpty()) {
            return result;
        }

        String[] lines = text.split("\\r?\\n");

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("#") || line.startsWith("//")) {
                continue;
            }

            String[] parts;

            if (line.contains("=")) {
                parts = line.split("=", 2);
            } else if (line.contains("->")) {
                parts = line.split("->", 2);
            } else {
                continue;
            }

            String rawSource = parts[0].trim();
            String rawReplacement = parts[1].trim();

            if (rawSource.isEmpty() || rawReplacement.isEmpty()) {
                continue;
            }

            String source = normalizeBlockId(rawSource);
            String replacement = normalizeBlockId(rawReplacement);

            result.put(source, replacement);
        }

        return result;
    }

    private static String normalizeBlockId(String rawId) {
        String id = rawId.trim();
        if (id.isEmpty()) {
            return id;
        }

        if (id.contains(":")) {
            return id;
        }

        return "minecraft:" + id;
    }
}
