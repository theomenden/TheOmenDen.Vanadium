package vanadium.mixin.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;
import vanadium.VanadiumConfig;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapTextureManagerMixin {

    @Shadow private float flickerIntensity;
    @Shadow @Final private MinecraftClient client;
    @Unique
    private float flickerTarget;

    @Unique
    private float flickerPosition;

    @Unique
    private int flickerTicksRemaining;

    @Unique
    private double relativeIntensityExponent;

    @Unique
    private int lightIndex = 0;

    @Unique
    private final int[] SKY_LIGHT_COLORS = new int[16];
    @Unique
    private final int[] BLOCK_LIGHT_COLORS = new int[16];

    @Inject(method="tick", at = @At("RETURN"))
    private void onTickingFlicker(CallbackInfo ci) {
        if(Vanadium.configuration.shouldFlickerBlockLight) {

            if(flickerTicksRemaining == 0) {
                flickerTicksRemaining = 4;
                flickerTarget = (float) Math.random();
            }

            flickerPosition = MathHelper.lerp(1.0f / flickerTicksRemaining, flickerPosition, flickerTarget);
            flickerTicksRemaining--;
            return;
        }

        flickerPosition = 0.0f;
        this.flickerIntensity = 0.0f;
    }

    @Inject(
            method = "update",
            at = @At(
                    value = "JUMP",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onUpdate(float partialTicks, CallbackInfo ci) {
        ClientWorld world = this.client.world;
        if(world == null) {
            return;
        }

        float ambience = (world.getSkyBrightness(partialTicks) - 0.2f) * 1.25f;
        relativeIntensityExponent = ambience * VanadiumConfig.calculateScale(Vanadium.configuration.relativeBlockLightIntensity);
    }

    @ModifyVariable(
            method = "update",
            at = @At(
                    value = "STORE",
                    ordinal = 0
            ),
            index = 16
    )
    private float modifyFlickerIntensity(float blockLight) {
        float result = blockLight;
        int sky = lightIndex >>> 4;
        int block = lightIndex & 0b1111;
        lightIndex = (lightIndex + 1) & 0xff;

        if (block != 15) {
            result = blockLight * (float) Math.exp(relativeIntensityExponent * sky);
        }
        return result;
    }
}
