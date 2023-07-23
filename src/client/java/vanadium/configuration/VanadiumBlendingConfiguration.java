package vanadium.configuration;

import me.jellysquid.mods.sodium.client.gui.options.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import vanadium.resources.VanadiumOptionsStorage;

public final class VanadiumBlendingConfiguration {

    public static OptionInstance<Integer> vanadiumBlendingRadius = new OptionInstance<>(
            "options.biomeBlendRadius",
            OptionInstance.noTooltip(),
            (component, integer) -> {
                int diameter = integer * 2 + 1;
                return Options.genericValueLabel(component, Component.translatable("options.biomeBlendRadius." + diameter));
            },
            new OptionInstance.IntRange(0, 14),
            14,
            (integer) -> {
                Minecraft.getInstance().levelRenderer.allChanged();
            });

    public static int getBiomeBlendingRadius() {
        return vanadiumBlendingRadius.get();
    }
}
