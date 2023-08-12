package vanadium;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name= VanadiumClient.MODID)
public class VanadiumConfig implements ConfigData {
    public boolean shouldClearSky;
    public boolean shouldClearVoid;
    public boolean shouldBlendSkyLighting;
    public boolean shouldFlickerBlockLight;
    public double relativeBlockLightIntensity = -13.0;

    @ConfigEntry.BoundedDiscrete(min=0, max = 14)
    public int blendingRadius = 14;
}
