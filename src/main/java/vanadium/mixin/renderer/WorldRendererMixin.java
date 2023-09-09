package vanadium.mixin.renderer;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;
import vanadium.Vanadium;

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
    @ModifyConstant(
            method = "renderSky",
            constant = @Constant(doubleValue = 0.0, ordinal = 0),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;getHorizonHeight(Lnet/minecraft/world/level/LevelHeightAccessor;)D"
                    )
            )
    )
    private double modifyVoidBackgroundCondition(double zero) {
        if(Vanadium.configuration.shouldClearVoid) {
            zero = Double.NEGATIVE_INFINITY;
        }
        return zero;
    }
}
