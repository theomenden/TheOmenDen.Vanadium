package vanadium.mixin.experience;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.entry.Vanadium;
import vanadium.util.MathUtils;

@Mixin(ExperienceOrbRenderer.class)
public abstract class XPOrbEntityRendererMixin extends EntityRenderer<ExperienceOrb> {
    private XPOrbEntityRendererMixin() {
        super(null);
    }

    @Unique
    private static boolean hasCustomColor;

    @Unique
    private static int customRed;
    @Unique
    private static int customGreen;
    @Unique
    private static int customBlue;

    @Inject(method = "render(Lnet/minecraft/world/entity/ExperienceOrb;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    at=@At("HEAD"))
    private void setColorOnRender(ExperienceOrb orb, float e, float partialTicks, PoseStack matrixStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if(Vanadium.EXPERIENCE_ORB_COLORS.hasCustomColorMapping()) {
            hasCustomColor = true;
            float ticksPerCycle = Vanadium.COLOR_PROPERTIES.getProperties().getXpOrbTime() / 50.0f;
            float fractionalTime = 1 - (float)Math.cos(((orb.invulnerableTime + partialTicks) * (MathUtils.PI2) /ticksPerCycle)) * 0.5f;
            int color = Vanadium.EXPERIENCE_ORB_COLORS.getFractionalColorMapping(fractionalTime);
            customRed = (color >> 16) & 0xff;
            customGreen = (color >> 8) & 0xff;
            customBlue = color  & 0xff;
            return;
        }

        hasCustomColor = false;
    }

    @Redirect(
            method = "vertex",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;color(IIII)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
            private static VertexConsumer proxyColor(VertexConsumer self, int red, int green, int blue, int alpha) {

        if(hasCustomColor){
            red = customRed;
            green = customGreen;
            blue = customBlue;
        }

        return self.color(red, green, blue, alpha);
    }

}
