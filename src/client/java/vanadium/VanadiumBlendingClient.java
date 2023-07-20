package vanadium;

import me.jellysquid.mods.sodium.client.gui.options.control.ControlElement;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;

public final class VanadiumBlendingClient {

    public static ControlElement<Integer> vanadiumBiomeBlendRadius =
            new SliderControl("options.biomeBlendRadius",0 ,14 , (integer) -> {
                Minecraft.getInstance().levelRenderer.allChanged();
            } , );

    public static int getBiomeBlendRadius() {
        return vanadiumBiomeBlendRadius.get();
    }

}
