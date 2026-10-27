
package com.donutprofit;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.Click;
import net.minecraft.text.Text;

public final class ProfitSettingsScreen extends Screen {

    private final Screen parent;
    private boolean dragging = false;

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
                Settings.saveBaseline(
                    DonutProfitClient.TRACKER
                );
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

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            b -> close()
        ).dimensions(cx - 105, height - 35, 210, 20).build());
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
            "Drag the preview to move the HUD",
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
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();

        int x = Math.round(Settings.x * width);
        int y = Math.round(Settings.y * height);

        if (
            click.button() == 0 &&
            mouseX >= x - 8 &&
            mouseX <= x + 260 &&
            mouseY >= y - 8 &&
            mouseY <= y + 30 * Settings.scale
        ) {
            dragging = true;
            return true;
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(
        Click click,
        double deltaX,
        double deltaY
    ) {
        if (dragging) {
            Settings.x = Math.max(
                0,
                Math.min(
                    0.95f,
                    (float) click.x() / width
                )
            );

            Settings.y = Math.max(
                0,
                Math.min(
                    0.9f,
                    (float) click.y() / height
                )
            );

            return true;
        }

        return super.mouseDragged(
            click,
            deltaX,
            deltaY
        );
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (dragging) {
            dragging = false;
            Settings.save();
            return true;
        }

        return super.mouseReleased(click);
    }

    @Override
    public void close() {
        Settings.save();
        client.setScreen(parent);
    }
}
