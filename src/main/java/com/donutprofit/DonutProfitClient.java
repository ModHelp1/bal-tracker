package com.donutprofit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;
import java.text.DecimalFormat;
import java.util.Locale;

public final class DonutProfitClient implements ClientModInitializer {
 public static final BalanceTracker TRACKER = new BalanceTracker();
 private static KeyBinding settingsKey, resetKey;
 private static final DecimalFormat FORMAT = new DecimalFormat("#,###");
 @Override public void onInitializeClient() {
  Settings.load();
  settingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.donutprofit.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.donutprofit"));
  resetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.donutprofit.reset", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, "category.donutprofit"));
  ClientReceiveMessageEvents.GAME.register((message, overlay) -> { if (TRACKER.acceptChat(message.getString())) Settings.saveBaseline(TRACKER); });
  ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> { if (TRACKER.acceptChat(message.getString())) Settings.saveBaseline(TRACKER); });
  ClientTickEvents.END_CLIENT_TICK.register(client -> {
   while (settingsKey.wasPressed()) client.setScreen(new ProfitSettingsScreen(client.currentScreen));
   while (resetKey.wasPressed()) { TRACKER.reset(); Settings.saveBaseline(TRACKER); }
  });
  HudRenderCallback.EVENT.register((context, tickCounter) -> drawHud(context));
 }
 private static void drawHud(DrawContext context) {
  MinecraftClient client = MinecraftClient.getInstance();
  if (client.player == null || client.options.hudHidden || !Settings.visible) return;
  String label = format(TRACKER.profit());
  int color = TRACKER.profit() >= 0 ? Settings.positiveColor : Settings.negativeColor;
  int x = Math.round(Settings.x * context.getScaledWindowWidth());
  int y = Math.round(Settings.y * context.getScaledWindowHeight());
  context.getMatrices().pushMatrix();
  context.getMatrices().translate(x, y);
  context.getMatrices().scale(Settings.scale, Settings.scale);
  int width = client.textRenderer.getWidth(label);
  context.fill(-5,-4,width+5,client.textRenderer.fontHeight+5,0x99000000);
  context.drawTextWithShadow(client.textRenderer,label,0,0,color);
  context.getMatrices().popMatrix();
 }
 public static String format(long value) {
  if (!TRACKER.initialized()) return "Profit: waiting for /bal";
  return (value >= 0 ? "+" : "-") + "$" + FORMAT.format(Math.abs(value));
 }
}
