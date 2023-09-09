package vanadium.mixin.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import vanadium.customcolors.mapping.LightMappings;
import vanadium.customcolors.mapping.Lightmap;
import vanadium.utils.MathUtils;

@Mixin(LightTexture.class)
public abstract class LightmapTextureManagerMixin {

    @Shadow private float blockLightRedFlicker;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private NativeImage lightPixels;
    @Shadow @Final private DynamicTexture lightTexture;

    @Shadow protected abstract float getDarknessGamma(float partialTick);

    @Shadow protected abstract float calculateDarknessScale(LivingEntity entity, float gamma, float partialTick);

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

    @Inject(
            method = "tick",
            at=@At("RETURN")
    )
    private void onTickFlicker(CallbackInfo ci) {
        if(Vanadium.configuration.shouldFlickerBlockLight) {

            if(flickerTicksRemaining == 0) {
                flickerTicksRemaining = 4;
                flickerTarget = (float) Math.random();
            }

            flickerPosition = Mth.lerp(1.0f/flickerTicksRemaining, flickerPosition, flickerTarget);
            flickerTicksRemaining--;
        } else {
            flickerPosition = 0.0f;
            this.blockLightRedFlicker = 0.0f;
        }
    }

    @Inject(
            method = "updateLightTexture",
            at = @At(
                    value = "JUMP",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void onLightTextureUpdate(float partialTicks, CallbackInfo ci) {
        ClientLevel level = this.minecraft.level;
        if(level == null) {
            return;
        }

        float ambience = (level.getStarBrightness(partialTicks) - 0.2f) * 1.25f;
        relativeIntensityExponent = ambience * VanadiumConfig.calculateScale(Vanadium.configuration.relativeBlockLightIntensity) * MathUtils.INV_16;

        if(level.getSkyFlashTime() > 0 ) {
            ambience = -1.0f;
        }

        Lightmap map = LightMappings.get(level);

        if(map != null) {
            float nightVision;
            Player player = this.minecraft.player;

            if(player.isUnderWater() && player.hasEffect(MobEffects.CONDUIT_POWER)) {
                nightVision = 1.0f;
            } else if(player.hasEffect(MobEffects.NIGHT_VISION)) {
                nightVision = GameRenderer.getNightVisionScale(player, partialTicks);
            } else {
                nightVision = 0.0f;
            }

            float brightness = this.minecraft.options.gamma().get().floatValue();
            float darknessScale = this.minecraft.options.darknessEffectScale().get().floatValue();
            float darknessFactor = darknessScale * this.getDarknessGamma(partialTicks);
            darknessFactor = darknessScale * this.calculateDarknessScale(player, darknessFactor, partialTicks);
            for(int i = 0; i < 16; i++) {
                SKY_LIGHT_COLORS[i] = map.getSkyLighting(i, ambience, nightVision);
                BLOCK_LIGHT_COLORS[i] = map.getBlockColorForLightLevel(i, flickerPosition, nightVision);
            }
            for(int skyLight = 0; skyLight < 16; skyLight++) {
                float blockIntensityScale = (float)Math.exp(relativeIntensityExponent * skyLight);
                int skyColor = SKY_LIGHT_COLORS[skyLight];
                int skyRed = (skyColor & 0xff0000) >> 16;
                int skyGreen = (skyColor & 0xff00) >> 8;
                int skyBlue = skyColor & 0xff;

                for(int blockLight = 0; blockLight < 16; blockLight++) {
                    int blockColor = BLOCK_LIGHT_COLORS[blockLight];
                    int blockRed = (blockColor & 0xff0000) >> 16;
                    int blockGreen = (blockColor & 0xff00) >> 8;
                    int blockBlue = blockColor & 0xff;

                    float scale = blockLight == 15 ? 1 : blockIntensityScale;
                    float r = Math.min(255.0f, skyRed + scale * blockRed) * MathUtils.INV_255;
                    float g = Math.min(255.0f, skyGreen + scale * blockGreen) * MathUtils.INV_255;
                    float b = Math.min(255.0f, skyBlue + scale * blockBlue) * MathUtils.INV_255;
                    r = Math.max(0.0f, r - darknessFactor);
                    g = Math.max(0.0f, g - darknessFactor);
                    b = Math.max(0.0f, b - darknessFactor);

                    float rbright = 1.0f - r;
                    rbright *= rbright * rbright * rbright;

                    float gbright = 1.0f - g;
                    gbright = gbright * gbright * gbright;

                    float bbright = 1.0f - b;
                    bbright = bbright * bbright * bbright;

                    r = r * (1.0f - brightness) + (1.0f - rbright) * brightness;
                    g = g * (1.0f - brightness) + (1.0f - gbright) * brightness;
                    b = b * (1.0f - brightness) + (1.0f - bbright) * brightness;
                    int color = 0xff000000
                            | (int)(r * 255.0f) << 16
                            | (int)(g * 255.0f) << 8
                            | (int)(b * 255.0f);
                    this.lightPixels.setPixelRGBA(blockLight, skyLight, color);
                }
            }
            this.lightTexture.upload();
            this.minecraft.getProfiler().pop();
            ci.cancel();
        }
    }

    @ModifyVariable(
            method = "updateLightTexture",
            at = @At("STORE"),
            ordinal = 2
    )
    private float modifySkyDarkness(float ambience) {
        if(!Vanadium.configuration.shouldBlendSkyLight) {
            ambience = (int)(ambience * 16) * MathUtils.INV_16F;
        }
        return ambience;
    }


    @ModifyVariable(
            method = "updateLightTexture",
            at = @At("STORE"),
            index = 16    )
    private float modifyFlickerIntensity(float blockLight) {
        int sky = lightIndex >>> 4;
        int block = lightIndex & 0b1111;
        lightIndex = (lightIndex + 1) & 0xff;
        if(block != 15) {
            return blockLight * (float)Math.exp(relativeIntensityExponent * sky);
        }
        return blockLight;
    }
}
