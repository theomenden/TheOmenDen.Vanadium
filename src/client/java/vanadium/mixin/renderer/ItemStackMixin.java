package vanadium.mixin.renderer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.Vanadium;

@Mixin(ItemStack.class)
public class ItemStackMixin {


    @Redirect(
    method= "getBarColor",
    at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item;getBarColor(Lnet/minecraft/world/item/ItemStack;)I",
            ordinal = 0
    ))
    private int proxyGetBarColor(Item instance, ItemStack stack) {
        if (!Vanadium.DURABILITY_COLORS.hasCustomColorMapping()) {
            return instance.getBarColor(stack);
        }

        float damage = stack.getDamageValue();
        float maxDamage = stack.getMaxDamage();
        float durability = Math.max(0.0f, (maxDamage - damage) / maxDamage);

        int color = Vanadium.DURABILITY_COLORS.getFractionalColorMapping(durability);

        return color;
    }


}
