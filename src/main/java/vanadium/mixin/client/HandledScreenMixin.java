package vanadium.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;

@Mixin(value = HandledScreen.class, priority = 899)
public abstract class HandledScreenMixin extends Screen {
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "drawSlot",
            at =@At(value="INVOKE",
            target="Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            shift = At.Shift.AFTER))
    private void onRendering(DrawContext context, Slot slot, CallbackInfo ci) {
        if(Vanadium.ITEMGUI_COLORS.hasCustomColorMapping()) {
                context.drawBorder(slot.x, slot.y,this.width,this.height, Vanadium.ITEMGUI_COLORS.getColorAtIndex(slot.getMaxItemCount()));
        }
    }
}
