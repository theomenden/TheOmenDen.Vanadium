package vanadium.mixin.coloring.xp;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;
import vanadium.utils.MathUtils;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(ExperienceOrbEntityRenderer.class)
public abstract class ExperienceOrbRendererMixin extends EntityRenderer<ExperienceOrbEntity> {
    private ExperienceOrbRendererMixin() {
        super(null);
    }

    @Unique
    private static boolean isCustom;

    @Unique
    private static int customRed;

    @Unique
    private static int customGreen;

    @Unique
    private static int customBlue;

    @Inject(method="render(Lnet/minecraft/entity/ExperienceOrbEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at=@At("HEAD"))
    private void onRender(ExperienceOrbEntity entity, float eh, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider provider, int int1, CallbackInfo info) {
        if(!VanadiumColormaticResolution.hasCustomXpOrbColors()) {
            isCustom = false;
        }

        isCustom = true;
        float ticksPerCycle = Vanadium.COLOR_PROPERTIES.getProperties().getXpOrbTime() * MathUtils.INV_50F;
        float fractional = (1 - MathHelper.cos((entity.age + partialTicks) * (float)(2 * MathUtils.PI2)/ ticksPerCycle)) / 2;

        var xpOrbColors = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.EXPERIENCE_ORB_COLORS,
                VanadiumColormaticResolution.COLORMATIC_EXPERIENCE_ORB_COLORS
        );

        int color = xpOrbColors.getFractionalColorMapping(fractional);

        customRed = (color >> 16) & 0xff;
        customGreen = (color >> 8) & 0xff;
        customBlue = color & 0xff;
    }

    @Redirect(
            method = "vertex",
            at = @At(
                    value="INVOKE",
                    target="Lnet/minecraft/client/render/VertexConsumer;color(IIII)Lnet/minecraft/client/render/VertexConsumer;"
            )
    )
    private static VertexConsumer proxyColor(VertexConsumer self, int r, int g, int b, int a) {
        if(isCustom) {

            r = customRed;
            g = customGreen;
            b = customBlue;
        }
        return self.color(r, g, b, a);
    }
}
