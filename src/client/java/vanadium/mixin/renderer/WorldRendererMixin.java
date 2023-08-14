package vanadium.mixin.renderer;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import vanadium.Vanadium;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @ModifyConstant(
            method="renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            constant = @Constant(doubleValue = 0.0, ordinal = 0),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/world/ClientWorld$Properties;getSkyDarknessHeight(Lnet/minecraft/world/HeightLimitView;)D"
                    )
            )
    )
    private double modifyVoidBackground(double zero) {
        if(Vanadium.configuration.shouldClearVoid) {
            zero = Double.NEGATIVE_INFINITY;
        }
        return zero;
    }
}
