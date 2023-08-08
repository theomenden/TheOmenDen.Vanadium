package vanadium.resources;

import vanadium.configuration.VanadiumConfig;

public class VanadiumOptionsStorage {
    private final VanadiumConfig options = VanadiumConfig.INSTANCE;
    public VanadiumConfig getData() {
        return this.options;
    }

}
