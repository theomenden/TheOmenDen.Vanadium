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
import vanadium.entry.Vanadium;
import vanadium.configuration.VanadiumConfig;
import vanadium.lightmapping.Lightmaps;
import vanadium.util.MathUtils;

import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

@Mixin(LightTexture.class)
public abstract class LightMapTextureRendererMixin {

    @Shadow private float blockLightRedFlicker;

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract float getDarknessGamma(float partialTick);

    @Shadow protected abstract float calculateDarknessScale(LivingEntity entity, float gamma, float partialTick);

    @Shadow @Final private NativeImage lightPixels;
    @Shadow @Final private DynamicTexture lightTexture;
    @Unique private int flickerTicksRemaining;
    @Unique private float flickerPosition;
    @Unique private float flickerTarget;
    @Unique private int lightIndex = 0;
    @Unique private final int[] SKY_LIGHTING_COLORS = new int[16];
    @Unique private final int[] BLOCK_LIGHTING_COLORS = new int[16];
    @Unique private double relativeIntensityExponentialScale;

    @Inject(method="tick",
    at = @At("RETURN"))
    private void onFlickeringTick(CallbackInfo ci) {
        if(Vanadium.getCurrentConfiguration().shouldFlickerBlockLight) {
            calculateTicksBetweenFlickerTargets();
        }

        flickerPosition = 0.0f;
        this.blockLightRedFlicker = 0.0f;
    }

    @Inject(
            method="updateLightTexture",
            at = @At(
                    value="JUMP",
                    ordinal = 1,
                    shift= At.Shift.BEFORE
    ),
            cancellable = true
    )
    private void onUpdateLightTexture(float partialTicks, CallbackInfo ci) {
        ClientLevel world = this.minecraft.level;

        if (world == null) {
            return;
        }

        float ambience = (world.getStarBrightness(partialTicks) - 0.2f) * 1.25f;

        relativeIntensityExponentialScale = ambience * VanadiumConfig.getScaledBlockLightIntensity(
          Vanadium.getCurrentConfiguration()
                  .relativeBlockLightIntensityExponent
        ) * MathUtils.INV_16;
        
        if(world.getSkyFlashTime() > 0) {
            ambience = -1.0f;
        }
        
        var map = Lightmaps.get(world);
        
        if(map != null) {
            float nightVision;
            Player player = this.minecraft.player;

            nightVision = calculateNightVisionStrengthForPlayer(partialTicks, player);

            var minecraftOptions = this.minecraft.options;

            float brightness = minecraftOptions
                    .gamma().get().floatValue();
            float darknessScale = minecraftOptions
                    .darknessEffectScale().get().floatValue();
            float darknessFactor = darknessScale * this.getDarknessGamma(partialTicks);

            darknessFactor =  darknessScale * this.calculateDarknessScale(player, darknessFactor, partialTicks);


            float finalAmbience = ambience;
            IntStream.range(0, 16)
                     .forEach(i -> {
                        SKY_LIGHTING_COLORS[i] = map.getBlockColorForLightLevel(i,
                                finalAmbience,
                                nightVision);
                        BLOCK_LIGHTING_COLORS[i] = map.getBlockColorForLightLevel(i,
                                flickerPosition,
                                nightVision);

                    });

            float finalDarknessFactor = darknessFactor;
            IntStream.range(0, 16)
                     .forEach(skyLight -> {
                        float blockIntensityScaling = (float)Math.exp(relativeIntensityExponentialScale * skyLight);

                        IntStream.range(0,16)
                                .forEach(blockLight -> {
                                    int skyColor = SKY_LIGHTING_COLORS[skyLight];
                                    int blockColor = BLOCK_LIGHTING_COLORS[blockLight];

                                    float scaleByBlockLight = blockLight == 15
                                            ? 1
                                            : blockIntensityScaling;

                                    float redValue = Math.min(255.0f, ((skyColor & 0xff0000) >> 16) + scaleByBlockLight *((blockColor & 0xff0000) >> 16) * MathUtils.INV_255);
                                    float greenValue  = Math.min(255.0f, ((skyColor & 0xff00) >> 8) + scaleByBlockLight *((blockColor & 0xff00) >> 8) * MathUtils.INV_255);
                                    float blueValue  = Math.min(255.0f, (skyColor & 0xff) + scaleByBlockLight *(blockColor & 0xff0000) * MathUtils.INV_255);

                                    redValue = Math.max(0.0f, redValue - finalDarknessFactor);
                                    greenValue = Math.max(0.0f, greenValue - finalDarknessFactor);
                                    blueValue = Math.max(0.0f, blueValue - finalDarknessFactor);

                                    float redBrightness = 1.0f - redValue;
                                    float greenBrightness = 1.0f - greenValue;
                                    float blueBrightness = 1.0f - blueValue;

                                    redBrightness = (float)Math.pow(redBrightness, 4);
                                    greenBrightness = (float)Math.pow(greenBrightness, 4);
                                    blueBrightness = (float)Math.pow(blueBrightness, 4);

                                    redBrightness = 1.0f - redBrightness;
                                    greenBrightness = 1.0f - greenBrightness;
                                    blueBrightness = 1.0f - blueBrightness;

                                    redValue = redValue * (1.0f - brightness) + redBrightness * brightness;
                                    greenValue = greenValue * (1.0f - brightness) + greenBrightness * brightness;
                                    blueValue = blueValue * (1.0f - brightness) + blueBrightness * brightness;

                                    int color = 0xff000000;

                                    color |= (int)(redValue * 255.0f) << 16;
                                    color |= (int)(greenValue * 255.0f) << 8;
                                    color |= (int)(blueValue * 255.0f);

                                    this.lightPixels.setPixelRGBA(blockLight, skyLight, color);
                                });
                    });

        }


        this.lightTexture.upload();
        this.minecraft.getProfiler().pop();
        ci.cancel();
    }

    @ModifyVariable(
            method="updateLightTexture",
            at = @At("STORE"),
            ordinal = 1
    )
    private float modifySkyAmbience(float value) {
        if(!Vanadium.getCurrentConfiguration().shouldBlendSkyLight) {
            value = (int)(value * 16) * MathUtils.INV_16F;
        }

        return value;
    }

    @ModifyVariable(
            method = "updateLightTexture",
            at = @At("STORE"),
            index= 16
    )
    private float modifyFlickerIntensity(float blockLightRedFlicker) {
        int sky = lightIndex >>> 4;
        int block = lightIndex & 0b1111;
        lightIndex = (lightIndex + 1) & 0xff;

        if(block != 15) {
            return blockLightRedFlicker * (float)Math.exp(relativeIntensityExponentialScale * sky);
        }
        return blockLightRedFlicker;
    }

    private static float calculateNightVisionStrengthForPlayer(float partialTicks, Player player) {
        if(player.isInWater()
        && player.hasEffect(MobEffects.CONDUIT_POWER)) {
            return 1.0f;
        }

        if(player.hasEffect(MobEffects.NIGHT_VISION)){
            return GameRenderer.getNightVisionScale(player, partialTicks);
        }

        return 0.0f;
    }

    private void calculateTicksBetweenFlickerTargets() {
        if(flickerTicksRemaining == 0) {
            flickerTicksRemaining = 4;
            flickerTarget = RandomGenerator.getDefault().nextFloat();
        }

        flickerPosition = Mth.lerp(1.0f / flickerTicksRemaining, flickerPosition, flickerTarget);
        flickerTicksRemaining--;
    }


}
