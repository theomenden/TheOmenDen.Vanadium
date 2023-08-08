package vanadium.mixin.renderer;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import vanadium.entry.Vanadium;

@Mixin(LevelRenderer.class)
public abstract class LevelWorldRendererMixin {
    @ModifyConstant(
            method = "renderSky",
            constant = @Constant(doubleValue = 0.0, ordinal = 0),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target ="Lnet/minecraft/client/multiplayer/ClientLevel;getSkyColor(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
                    )
            )
    )
    private double modifyVoidBackgroundRenderingCondition(double zero) {
        if(Vanadium.getCurrentConfiguration().shouldClearVoid) {
            zero = Double.NEGATIVE_INFINITY;
        }
        return zero;
    }
}
