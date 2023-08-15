package vanadium;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name= vanadium.Vanadium.MODID)
public class VanadiumConfig implements ConfigData {
    public boolean shouldClearSky;
    public boolean shouldClearVoid;
    public boolean shouldBlendSkyLighting;
    public boolean shouldFlickerBlockLight;
    public double relativeBlockLightIntensity = -13.0;

    @ConfigEntry.BoundedDiscrete(min=0, max = 14)
    public int blendingRadius = 14;

    public static double calculateScale(double relativeBlockLightIntensity) {
        final double LOG_2 = Math.log(2);
        return LOG_2 * 0.25 * relativeBlockLightIntensity;
    }
}
