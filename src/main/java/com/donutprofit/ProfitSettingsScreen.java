
package com.donutprofit;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class ProfitSettingsScreen extends Screen {

    private final Screen parent;


    ProfitSettingsScreen(Screen parent) {
        super(Text.literal("demo16's bal tracker"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Reset profit to $0"),
            b -> {
                DonutProfitClient.TRACKER.reset();
                Settings.saveBaseline(DonutProfitClient.TRACKER);
            }
        ).dimensions(cx - 105, height - 115, 210, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Size -"),
            b -> {
                Settings.scale = Math.max(
                    0.5f, Settings.scale - 0.25f
                );
                Settings.save();
            }
        ).dimensions(cx - 105, height - 90, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Size +"),
            b -> {
                Settings.scale = Math.min(
                    4f, Settings.scale + 0.25f
                );
                Settings.save();
            }
        ).dimensions(cx + 5, height - 90, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Profit color"),
            b -> {
                Settings.positiveColor =
                    nextColor(Settings.positiveColor);
                Settings.save();
            }
        ).dimensions(cx - 105, height - 65, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Loss color"),
            b -> {
                Settings.negativeColor =
                    nextColor(Settings.negativeColor);
                Settings.save();
            }
        ).dimensions(cx + 5, height - 65, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Left"), b -> move(-0.02f, 0f))
            .dimensions(cx - 105, height - 140, 48, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Right"), b -> move(0.02f, 0f))
            .dimensions(cx - 52, height - 140, 48, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Up"), b -> move(0f, -0.02f))
            .dimensions(cx + 5, height - 140, 48, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Down"), b -> move(0f, 0.02f))
            .dimensions(cx + 58, height - 140, 48, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            b -> close()
        ).dimensions(cx - 105, height - 35, 210, 20).build());
    }

    private static void move(float dx, float dy) {
        Settings.x = Math.max(0f, Math.min(0.95f, Settings.x + dx));
        Settings.y = Math.max(0f, Math.min(0.90f, Settings.y + dy));
        Settings.save();
    }

    private static int nextColor(int current) {
        int[] colors = {
            0x55FF55,
            0xFF5555,
            0x55FFFF,
            0xFFFF55,
            0xFFFFFF,
            0xFF55FF,
            0xFFAA00
        };

        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == current) {
                return colors[(i + 1) % colors.length];
            }
        }

        return colors[0];
    }

    @Override
    public void render(
        DrawContext context,
        int mouseX,
        int mouseY,
        float delta
    ) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
            textRenderer,
            "Use the arrow buttons to move the HUD",
            width / 2,
            25,
            0xFFFFFFFF
        );

        int x = Math.round(Settings.x * width);
        int y = Math.round(Settings.y * height);

        String sample = DonutProfitClient.format(
            DonutProfitClient.TRACKER.profit()
        );

        int color = DonutProfitClient.TRACKER.profit() >= 0
            ? Settings.positiveColor
            : Settings.negativeColor;

        context.fill(
            x - 5,
            y - 5,
            x + Math.round(
                textRenderer.getWidth(sample) * Settings.scale
            ) + 8,
            y + Math.round(12 * Settings.scale) + 5,
            0xAA000000
        );

        context.getMatrices().pushMatrix();
        context.getMatrices().translate(x, y);
        context.getMatrices().scale(
            Settings.scale,
            Settings.scale
        );

        context.drawTextWithShadow(
            textRenderer,
            sample,
            0,
            0,
            color
        );

        context.getMatrices().popMatrix();
    }

    @Override
    public void close() {
        Settings.save();
        client.setScreen(parent);
    }
}
