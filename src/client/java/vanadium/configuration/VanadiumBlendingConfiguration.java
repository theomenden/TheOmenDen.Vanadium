package vanadium.configuration;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

public final class VanadiumBlendingConfiguration {

    public static OptionInstance<Integer> vanadiumBlendingRadius = new OptionInstance<>(
            "category.biomeBlendRadius",
            OptionInstance.noTooltip(),
            (component, integer) -> {
                int diameter = integer * 2 + 1;
                return Options.genericValueLabel(component, Component.translatable("text.autoconfig.options.vanadiumBlendingRadius." + diameter));
            },
            new OptionInstance.IntRange(0, 14),
            14,
            (integer) -> {
                Minecraft.getInstance().levelRenderer.allChanged();
            });

    public static int getBiomeBlendingRadius() {
        return vanadiumBlendingRadius.get();
    }

    public static void setBiomeBlendingRadius(int blendingRadius) { vanadiumBlendingRadius.set(blendingRadius);}
}
