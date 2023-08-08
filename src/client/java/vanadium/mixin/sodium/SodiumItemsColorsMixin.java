package vanadium.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.color.interop.ItemColorsExtended;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import vanadium.colormapping.BiomeColorMappings;

@Mixin(value = ItemColors.class, priority = 2000)
@Implements(
        @Interface(iface = ItemColorsExtended.class,
        prefix = "i$",
        remap = Interface.Remap.NONE)
)
public abstract class SodiumItemsColorsMixin implements ItemColorsExtended {
@Unique private static final ItemColor VANADIUM_PROVIDER = (stack, tintIndex) ->
        BiomeColorMappings.getBiomeColorMapping(((BlockItem)stack.getItem()).getBlock().defaultBlockState(), null, null);

@Intrinsic(displace = true)
    public ItemColor i$sodium$getColorProvider(ItemStack stack) {
    if(stack.getItem() instanceof BlockItem blockItem
        && BiomeColorMappings.isItemCustomColored(blockItem.getBlock().defaultBlockState())) {
        return VANADIUM_PROVIDER;
    }
    return this.sodium$getColorProvider(stack);
}
}
