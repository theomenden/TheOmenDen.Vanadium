package vanadium.configuration;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import vanadium.entry.Vanadium;

@Config(name= Vanadium.MODID)
public class VanadiumConfig implements ConfigData {
    public static final VanadiumConfig INSTANCE = new VanadiumConfig();

    public boolean shouldClearSky;
    public boolean shouldClearVoid;
    public boolean shouldBlendSkyLight = true;
    public boolean shouldFlickerBlockLight = true;
    public double relativeBlockLightIntensityExponent = -13.0;

    public int blendingRadius = 14;

    public static OptionInstance<Integer> vanadiumBlendingRadius = new OptionInstance<>(
            "options.biomeBlendRadius",
            OptionInstance.noTooltip(),
            (component, integer) -> {
                int diameter = integer * 2 + 1;
                return Options.genericValueLabel(component, Component.translatable("options.biomeBlendRadius." + diameter));
            },
            new OptionInstance.IntRange(0, 14),
            14,
            (integer) -> {
                Minecraft.getInstance().levelRenderer.allChanged();
            });

    public static int getBiomeBlendingRadius() {
        return vanadiumBlendingRadius.get();
    }

    public static double getScaledBlockLightIntensity(double relativeBlockLightIntensityExponent) {
        return Math.log(2) * 0.25 * relativeBlockLightIntensityExponent;
    }
}
