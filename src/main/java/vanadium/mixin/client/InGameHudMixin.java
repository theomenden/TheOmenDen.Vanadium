package vanadium.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int scaledHeight;

    @Shadow private int scaledWidth;

    @ModifyConstant(
            method="renderExperienceBar",
            constant = @Constant(intValue = 8453920)
    )
    private int getExperienceTextColor(int original) {
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES
        );
        int color = colorProperties.getProperties().getXpText();
        return color != 0
                ? color
                : original;
    }

    @Inject(method="renderHotbarItem",
    at = @At(value="INVOKE",
    target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V",
    shift = At.Shift.AFTER)
    )
    private void onItemSlotRender(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if(Vanadium.ITEMGUI_COLORS.hasCustomColorMapping()) {
            context.drawBorder(x, y,this.scaledWidth ,this.scaledHeight,Vanadium.ITEMGUI_COLORS.getColorAtIndex(stack.getCount()));
        }
    }
}
