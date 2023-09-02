package vanadium.mixin.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
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
import vanadium.customcolors.mapping.LightMappings;
import vanadium.customcolors.mapping.Lightmap;
import vanadium.utils.MathUtils;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapTextureManagerMixin {

    @Shadow private float flickerIntensity;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private NativeImage image;
    @Shadow @Final private NativeImageBackedTexture texture;

    @Shadow protected abstract float getDarknessFactor(float delta);

    @Shadow protected abstract float getDarkness(LivingEntity entity, float factor, float delta);

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
        if (world == null) {
            return;
        }

        float ambience = (world.getSkyBrightness(partialTicks) - 0.2f) * 1.25f;
        relativeIntensityExponent = ambience * VanadiumConfig.calculateScale(Vanadium.configuration.relativeBlockLightIntensity);

        Lightmap map = LightMappings.get(world);
        if (map != null) {
            float nightVision;
            PlayerEntity player = this.client.player;
            if (player.isSubmergedInWater() && player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                nightVision = 1.0f;
            } else if (player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                nightVision = GameRenderer.getNightVisionStrength(player, partialTicks);
            } else {
                nightVision = 0.0f;
            }
            float brightness = this.client.options
                    .getGamma()
                    .getValue()
                    .floatValue();
            float darknessScale = this.client.options
                    .getDarknessEffectScale()
                    .getValue()
                    .floatValue();
            float darknessFactor = darknessScale * this.getDarknessFactor(partialTicks);
            darknessFactor = darknessScale * this.getDarkness(player, darknessFactor, partialTicks);
            for (int i = 0; i < 16; i++) {
                SKY_LIGHT_COLORS[i] = map.getSkyLighting(i, ambience, nightVision);
                BLOCK_LIGHT_COLORS[i] = map.getBlockColorForLightLevel(i, flickerPosition, nightVision);
            }
            for (int skyLight = 0; skyLight < 16; skyLight++) {
                float blockIntensityScale = (float) Math.exp(relativeIntensityExponent * skyLight);
                for (int blockLight = 0; blockLight < 16; blockLight++) {
                    int skyColor = SKY_LIGHT_COLORS[skyLight];
                    int blockColor = BLOCK_LIGHT_COLORS[blockLight];
                    // color will add the channels and cap at white
                    float scale = blockLight == 15 ? 1 : blockIntensityScale;
                    float r = Math.min(255.0f, ((skyColor & 0xff0000) >> 16) + scale * ((blockColor & 0xff0000) >> 16)) / 255.0f;
                    float g = Math.min(255.0f, ((skyColor & 0xff00) >> 8) + scale * ((blockColor & 0xff00) >> 8)) / 255.0f;
                    float b = Math.min(255.0f, (skyColor & 0xff) + scale * (blockColor & 0xff)) / 255.0f;
                    r = Math.max(0.0f, r - darknessFactor);
                    g = Math.max(0.0f, g - darknessFactor);
                    b = Math.max(0.0f, b - darknessFactor);
                    float rbright = 1.0f - r;
                    float gbright = 1.0f - g;
                    float bbright = 1.0f - b;
                    rbright *= rbright;
                    gbright *= gbright;
                    bbright *= bbright;
                    rbright *= rbright;
                    gbright *= gbright;
                    bbright *= bbright;
                    rbright = 1.0f - rbright;
                    gbright = 1.0f - gbright;
                    bbright = 1.0f - bbright;
                    r = r * (1.0f - brightness) + rbright * brightness;
                    g = g * (1.0f - brightness) + gbright * brightness;
                    b = b * (1.0f - brightness) + bbright * brightness;
                    int color = 0xff000000;
                    color |= (int) (r * 255.0f) << 16;
                    color |= (int) (g * 255.0f) << 8;
                    color |= (int) (b * 255.0f);
                    this.image.setColor(blockLight, skyLight, color);
                }
            }
            // do the cleanup because we cancel the default
            this.texture.upload();
            this.client
                    .getProfiler()
                    .pop();
            ci.cancel();
        }
    }

    @ModifyVariable(
            method="update",
            at = @At(
                    value = "STORE",
                    ordinal = 1
            ),
            ordinal = 1
    )
    private float modifySkyAmbience(float delta) {
        if(Vanadium.configuration.shouldBlendSkyLight) {
            delta = (int)(delta * 16) * MathUtils.INV_16F;
        }
        return delta;
    }

    @ModifyVariable(
            method = "update",
            at = @At(
                    value = "STORE",
                    ordinal = 1
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
