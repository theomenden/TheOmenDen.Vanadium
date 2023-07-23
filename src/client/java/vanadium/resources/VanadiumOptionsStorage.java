package vanadium.resources;

import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;
import vanadium.configuration.VanadiumConfig;

public class VanadiumOptionsStorage implements OptionStorage<VanadiumConfig> {
    private final VanadiumConfig options = VanadiumConfig.INSTANCE;
    @Override
    public VanadiumConfig getData() {
        return this.options;
    }

    @Override
    public void save() {
        this.options.serializeConfigToJsonAsync();
    }
}
