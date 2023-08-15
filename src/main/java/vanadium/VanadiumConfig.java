package vanadium;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name= Vanadium.MODID)
public class VanadiumConfig implements ConfigData {

    @ConfigEntry.Category("customColors")
    @Tooltip()
    public boolean shouldClearSky = false;
    @ConfigEntry.Category("customColors")
    @Tooltip()
    public boolean shouldClearVoid = false;
    @ConfigEntry.Category("customColors")
    @Tooltip()
    public boolean shouldBlendSkyLight = true;
    @ConfigEntry.Category("customColors")
    @Tooltip()
    public boolean shouldFlickerBlockLight = true;
    @ConfigEntry.Category("customColors")
    @Tooltip()
    public double relativeBlockLightIntensity = -13.0;

    @ConfigEntry.Category("biomeBlendRadius")
    @Tooltip()
    @ConfigEntry.BoundedDiscrete(min=0, max = 14)
    public int blendingRadius = 14;

    public static double calculateScale(double relativeBlockLightIntensity) {
        final double LOG_2 = Math.log(2);
        return LOG_2 * 0.25 * relativeBlockLightIntensity;
    }
}
