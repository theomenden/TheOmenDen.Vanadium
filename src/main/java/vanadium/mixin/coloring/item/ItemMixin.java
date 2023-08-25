package vanadium.mixin.coloring.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(
            method = "getItemBarColor",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onGetItemBar(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if(Vanadium.DURABILITY_COLORS.hasCustomColorMapping()) {
            float damage = stack.getDamage();
            float maxDamage = stack.getMaxDamage();
            float durability = Math.max(0.0f, (maxDamage - damage) / maxDamage);
            int color = Vanadium.DURABILITY_COLORS.getFractionalColorMapping(durability);

            cir.setReturnValue(color);
        }
    }

}
