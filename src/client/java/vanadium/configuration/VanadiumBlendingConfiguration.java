package vanadium.configuration;

import me.jellysquid.mods.sodium.client.gui.options.Option;
import vanadium.resources.VanadiumOptionsStorage;

public final class VanadiumBlendingConfiguration {

    private static final VanadiumOptionsStorage  vanadiumOptions = new VanadiumOptionsStorage();
    public static Option<Integer> vanadiumBiomeBlendRadius;

    public static int getBlendingRadius() {
        return vanadiumBiomeBlendRadius.getValue().intValue();
    }
}
