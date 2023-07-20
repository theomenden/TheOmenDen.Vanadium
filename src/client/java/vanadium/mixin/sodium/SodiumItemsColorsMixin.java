package vanadium.mixin.sodium;

import me.jellysquid.mods.sodium.client.world.biome.ItemColorsExtended;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import vanadium.colormapping.BiomeColorMappings;

@Mixin(value = ItemColors.class, priority = 2000)
@Implements(
        @Interface(iface = ItemColorsExtended.class,
        prefix = "i$",
        remap = Interface.Remap.NONE)
)
public abstract class SodiumItemsColorsMixin implements ItemColorsExtended {
@Unique private static final ItemColorProvider VANADIUM_PROVIDER = (stack, tintIndex) ->
        BiomeColorMappings.getBiomeColorMapping(((BlockItem)stack.getItem()).getBlock().getDefaultState(), null, null);

@Intrinsic(displace = true)
    public ItemColorProvider i$getColorProvider(ItemStack stack) {
    if(stack.getItem() instanceof BlockItem blockItem
        && BiomeColorMappings.isItemCustomColored(blockItem.getBlock().getDefaultState())) {
        return VANADIUM_PROVIDER;
    }
    return this.getColorProvider(stack);
}
}
