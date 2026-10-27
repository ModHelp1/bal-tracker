package com.donutprofit;

import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class Settings {
 static float x = 0.02f, y = 0.15f, scale = 1.5f;
 static int positiveColor = 0x55FF55, negativeColor = 0xFF5555;
 static boolean visible = true;
 private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("donutprofit.properties");
 private static long baseline = 0;
 private static boolean hasBaseline = false;
 static void load() {
  Properties p = new Properties();
  try (var in = Files.newInputStream(PATH)) { p.load(in); } catch (Exception ignored) { return; }
  try {
   x = Float.parseFloat(p.getProperty("x","0.02")); y = Float.parseFloat(p.getProperty("y","0.15"));
   scale = Float.parseFloat(p.getProperty("scale","1.5"));
   positiveColor = Integer.parseInt(p.getProperty("positive","55FF55"),16);
   negativeColor = Integer.parseInt(p.getProperty("negative","FF5555"),16);
   visible = Boolean.parseBoolean(p.getProperty("visible","true"));
   hasBaseline = Boolean.parseBoolean(p.getProperty("hasBaseline","false"));
   baseline = Long.parseLong(p.getProperty("baseline","0"));
   if (hasBaseline) DonutProfitClient.TRACKER.setBaseline(baseline);
  } catch (Exception ignored) { }
 }
 static void saveBaseline(BalanceTracker tracker) { if (tracker.initialized()) { baseline = tracker.baseline(); hasBaseline = true; } save(); }
 static void save() {
  Properties p = new Properties();
  p.setProperty("x",Float.toString(x)); p.setProperty("y",Float.toString(y)); p.setProperty("scale",Float.toString(scale));
  p.setProperty("positive",Integer.toHexString(positiveColor)); p.setProperty("negative",Integer.toHexString(negativeColor));
  p.setProperty("visible",Boolean.toString(visible)); p.setProperty("hasBaseline",Boolean.toString(hasBaseline)); p.setProperty("baseline",Long.toString(baseline));
  try { Files.createDirectories(PATH.getParent()); try (var out = Files.newOutputStream(PATH)) { p.store(out,"Donut Profit Tracker"); } } catch (Exception ignored) { }
 }
}
