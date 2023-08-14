package vanadium.mixin.coloring.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.customcolors.mapping.BiomeColorMappings;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin {
    @Inject(
            method = "getColor",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onColorMultiplier(ItemStack stack, int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(stack.getItem() instanceof BlockItem) {
            BlockState state = ((BlockItem)stack.getItem())
                    .getBlock()
                    .getDefaultState();

            if(BiomeColorMappings.isItemCustomColored(state)) {
                int color = BiomeColorMappings.getBiomeColorMapping(state, null, null);
                cir.setReturnValue(color);
            }
        }
    }
}
