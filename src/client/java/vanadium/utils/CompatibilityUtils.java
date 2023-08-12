package vanadium.utils;

import net.fabricmc.loader.api.FabricLoader;

public class CompatibilityUtils {
    public static final boolean IS_SODIUM_LOADED = FabricLoader.getInstance()
                                                               .isModLoaded("sodium");
}
