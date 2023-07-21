package vanadium;

import com.mojang.brigadier.CommandDispatcher;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlElement;
import me.jellysquid.mods.sodium.client.gui.options.control.SliderControl;
import net.minecraft.client.option.SimpleOption;

public final class VanadiumBlendingClient {

    public static SimpleOption<Integer> vanadiumBiomeBlendRadius = new SimpleOption<>();

    public static int getBlendingRadius() {
        return vanadiumBiomeBlendRadius.get();
    }
}
