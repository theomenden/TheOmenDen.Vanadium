package vanadium;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import vanadium.configuration.VanadiumConfig;
import vanadium.configuration.VanadiumConfigScreen;

public class VanadiumModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new VanadiumConfigScreen(parent, VanadiumConfig.INSTANCE);
    }
}
