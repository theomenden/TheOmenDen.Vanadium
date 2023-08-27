package vanadium.mixin.renderer;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import org.apache.commons.lang3.ObjectUtils;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.customcolors.resources.BiomeColorMappingResource;
import vanadium.models.records.Coordinates;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
    @Shadow
    private static float red;
    @Shadow
    private static float green;
    @Shadow
    private static float blue;
    @Unique
    private static float redStore;
    @Unique
    private static float greenStore;
    @Unique
    private static float blueStore;

    @ModifyVariable(
            method="render",
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private static CameraSubmersionType detectWhenVanadiumShouldBlendFluids(CameraSubmersionType submersionType) {
        if(isSubmersedColor(submersionType)) {
            return CameraSubmersionType.WATER;
        }
        return submersionType;
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target="Lnet/minecraft/world/biome/Biome;getWaterFogColor()I"
            )
    )
    private static int proxyLavaFogColorBlending(Biome biome, Camera camera, float tickDelta, ClientWorld world, int i, float f) {
        Fluid fluid;
        BiomeColorMappingResource colorMappingResource;

        var submersionType = camera.getSubmersionType();

        if(submersionType == CameraSubmersionType.LAVA) {
            fluid = Fluids.LAVA;

            colorMappingResource = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.UNDERLAVA_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_UNDERLAVA_COLORS
            );
        } else {
            fluid = Fluids.WATER;
            colorMappingResource = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.UNDERWATER_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_UNDERWATER_COLORS
            );
        }

        int color = 0;

        if(BiomeColorMappings.isFluidFogCustomColored(fluid)) {
            BiomeColorMapping colorMapping = BiomeColorMappings.getFluidFog(world.getRegistryManager(), fluid, biome);

            if(colorMapping != null){
                BlockPos position = camera.getBlockPos();
                var cameraCoordinates = new Coordinates(position.getX(), position.getY(), position.getZ());
                color = colorMapping.getColorAtCoordinatesForBiome(world.getRegistryManager(), biome, cameraCoordinates);
            }
        }

        if(color == 0) {
            if(colorMappingResource.hasCustomColorMapping()) {
                BlockPos  pos = camera.getBlockPos();
                var cameraCoordinates = new Coordinates(pos.getX(), pos.getY(), pos.getZ());
                color = colorMappingResource
                        .getColorMapping()
                        .getColorAtCoordinatesForBiome(world.getRegistryManager(), biome, cameraCoordinates);
            } else {
                if(submersionType == CameraSubmersionType.LAVA) {
                    color = 0x991900;
                } else {
                    color = biome.getWaterFogColor();
                }
            }
        }

        return color;
    }

    @Dynamic("RgbFetcher lambda method in #render")
    @Redirect(
            method = "method_24873",
            at = @At(
                    value = "INVOKE",
                    target= "Lnet/minecraft/world/biome/Biome;getFogColor()I"
            )
    )
    private static int proxyGetFogColor(Biome self, ClientWorld world, BiomeAccess access, float angleDelta, int x, int y, int z) {
        if(Vanadium.configuration.shouldClearSky
        && world.getDimension().hasSkyLight()) {
            return self.getFogColor();
        }

        var dimId = Vanadium.getDimensionid(world);

        VanadiumResolver resolver = BiomeColorMappings.getTotalSkyFog(dimId);
        return resolver
                .getColorAtCoordinatesForBiome(world.getRegistryManager(), self, new Coordinates(BiomeCoords.toBlock(x), BiomeCoords.toBlock(y),BiomeCoords.toBlock(z)));
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "FIELD",
                    target= "Lnet/minecraft/client/render/BackgroundRenderer;blue:F",
                    opcode = Opcodes.PUTSTATIC,
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            slice = @Slice (
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/util/CubicSampler;sampleColor(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/CubicSampler$RgbFetcher;)Lnet/minecraft/util/math/Vec3d;"
                    )
            )
    )
    private static void setFogColorToSkyColor(Camera camera, float partialTicks, ClientWorld world, int i, float f, CallbackInfo ci) {
        if(Vanadium.configuration.shouldClearSky
        && world.getDimension().hasSkyLight()) {
            Vec3d color = world.getSkyColor(camera.getPos(), partialTicks);

            red = (float)color.x;
            green = (float)color.y;
            blue = (float)color.z;
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"
            )
    )
    private static void saveColorsOnWeathering(CallbackInfo ci) {
        if(Vanadium.configuration.shouldClearSky) {
            redStore = BackgroundRendererMixin.red;
            greenStore = BackgroundRendererMixin.green;
            blueStore = BackgroundRendererMixin.blue;
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/BackgroundRenderer;lastWaterFogColorUpdateTime:J"
            ),
            slice = @Slice (
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/world/ClientWorld;getRainGradient(F)F"
                    )
            )
    )
    private static void resetWeatheringColors(CallbackInfo ci) {
        if(Vanadium.configuration.shouldClearSky) {
            red = redStore;
            green = greenStore;
            blue = blueStore;
        }
    }

    @ModifyVariable(
            method = "render",
            at = @At(value = "STORE", ordinal = 2),
            index=7
    )
    private static float modifyVoidColor(float scale) {
        if(Vanadium.configuration.shouldClearVoid) {
            scale = 1.0f;
        }
        return scale;
    }

    @Unique
    private static boolean isSubmersedColor(CameraSubmersionType submersionType) {
        return submersionType == CameraSubmersionType.LAVA
                && (
                BiomeColorMappings.isFluidFogCustomColored(Fluids.LAVA)
                || VanadiumColormaticResolution.hasCustomUnderLavaColors()
                );
    }
}
