package vanadium.mixin.renderer;

import net.fabricmc.fabric.impl.biome.BiomeSourceAccess;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.entry.Vanadium;
import vanadium.colormapping.BiomeColorMap;
import vanadium.colormapping.BiomeColorMappings;
import vanadium.models.Coordinates;
import vanadium.resolvers.VanadiumResolver;
import vanadium.resources.BiomeColorMappingResource;

@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin {

    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Unique private static float redStore;
    @Unique private static float blueStore;
    @Unique private static float greenStore;

    @ModifyVariable(
            method = "setupColor",
            at = @At(
                    value = "LOAD",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private static FogType detectWhenVanadiumFluidBlendingShouldHappen(FogType fogType) {
        if(isCameraSubmergedInLava(fogType)) {
            return FogType.WATER;
        }

        return fogType;
    }

    @Redirect(
            method = "setupColor",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;getWaterFogColor()I"
            )
    )
    private static int proxyWaterLavaFogColor(Biome biome, Camera camera, float tickDelta, ClientLevel level, int i, float f) {
        Fluid fluid;
        BiomeColorMappingResource colorMappingResource;

        var submersionType = camera.getFluidInCamera();

        if(submersionType == FogType.LAVA) {
            fluid = Fluids.LAVA;
            colorMappingResource = Vanadium.UNDERLAVA_COLORS;
        } else {
            fluid = Fluids.WATER;
            colorMappingResource = Vanadium.UNDERWATER_COLORS;
        }

        int color = 0;

        if(BiomeColorMappings.isFluidFogCustomColored(fluid)) {
            BiomeColorMap colorMapping = BiomeColorMappings.getFluidFog(level.registryAccess(), fluid, biome);

            if(colorMapping != null) {
                BlockPos position = camera.getBlockPosition();
                color = colorMapping.getColorAtCoordinatesForBiomeByManager(level.registryAccess(), biome, new Coordinates(position.getX(), position.getY(), position.getZ()));
            }
        }

        if(color == 0
        && colorMappingResource.hasCustomColorMapping()) {
            BlockPos pos = camera.getBlockPosition();
            color = colorMappingResource
                    .getColorMapping()
                    .getColorAtCoordinatesForBiomeByManager(level.registryAccess(),
                            biome,
                            new Coordinates(pos.getX(), pos.getY(), pos.getZ()));
        } else if(submersionType == FogType.LAVA) {
            color = 0x991900;
        } else {
            color = biome.getWaterFogColor();
        }

        return color;
    }

    @Dynamic("RgbFetcher lambda method in #render")
    @Redirect(method = "method_24873",
    at = @At(
            value = "INVOKE",
            target= "Lnet/minecraft/world/level/biome/Biome;getFogColor()I"
    ))
    private static int proxyCustomFogColor(Biome self, ClientLevel level, BiomeSourceAccess access,
                                           float angleDelta, int x, int y, int z) {

        if(Vanadium.getCurrentConfiguration().getShouldClearSky()
         && level.dimensionType().hasSkyLight()) {
            return self.getFogColor();
        }

        var dimensionId = Vanadium.getDimensionId(level);
        VanadiumResolver resolver = BiomeColorMappings.getTotalSkyFog(dimensionId);
        return resolver.getColorAtCoordinatesForBiomeByManager(
                level.registryAccess(),
                self,
                new Coordinates(QuartPos.toBlock(x), QuartPos.toBlock(y), QuartPos.toBlock(z))
        );
    }

    @Inject(
            method = "setupColor",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/FogRenderer;fogBlue:F",
                    opcode = Opcodes.PUTSTATIC,
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            slice = @Slice (
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"
                    )
            )
    )
    private static void setFogColorToSkyColor(Camera camera, float partialTicks, ClientLevel level, int i , float f, CallbackInfo ci) {
        if (!Vanadium
                .getCurrentConfiguration()
                .getShouldClearSky()
                || !level
                .dimensionType()
                .hasSkyLight()) {
            return;
        }

        Vec3 color = level.getSkyColor(camera.getPosition(), partialTicks);

        BackgroundRendererMixin.fogRed = (float)color.x();
        BackgroundRendererMixin.fogGreen = (float)color.y();
        BackgroundRendererMixin.fogBlue = (float)color.z();


    }

    @Inject(
            method = "setupColor",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getWaterVision()F")
    )
    private static void saveColorsToRestRainAndThunder(CallbackInfo ci) {
        if(!Vanadium.getCurrentConfiguration().getShouldClearSky()) {
            return;
        }

        redStore = BackgroundRendererMixin.fogRed;
        greenStore = BackgroundRendererMixin.fogGreen;
        blueStore = BackgroundRendererMixin.fogBlue;
    }

    @Inject(
            method = "setupColor",
            at = @At(
                    value = "FIELD",
                    target= "Lnet/minecraft/client/renderer/FogRenderer;biomeChangedTime:J"
            ),
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
                    )
            )
    )
    private static void resetRainAndThunderColors(CallbackInfo ci) {
        if(!Vanadium.getCurrentConfiguration().getShouldClearSky()) {
            return;
        }

        BackgroundRendererMixin.fogRed = redStore;
        BackgroundRendererMixin.fogGreen = greenStore;
        BackgroundRendererMixin.fogBlue = blueStore;
    }

    @ModifyVariable(method="setupColor",
    at = @At(value = "STORE", ordinal = 2),
    index = 7)
    private static float modifyVoidColor(float scale) {
        if(Vanadium.getCurrentConfiguration().getShouldClearVoid()) {
            scale = 1.0f;
        }
        return scale;
    }


    private static boolean isCameraSubmergedInLava(FogType fogType) {
        return fogType ==  FogType.LAVA
                && BiomeColorMappings.isFluidFogCustomColored(Fluids.LAVA)
                || Vanadium.UNDERLAVA_COLORS.hasCustomColorMapping();
    }
}


