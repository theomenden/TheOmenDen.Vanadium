package vanadium.mixin.renderer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.Vanadium;
import vanadium.customcolors.interfaces.VanadiumResolver;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.models.records.Coordinates;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(FogRenderer.class)
public abstract class BackgroundRendererMixin {

    @ModifyVariable(
            method = "setupColor",
            at = @At(
                    value="LOAD",
                    ordinal = 0
            ),
            ordinal = 0
    )
    private static FogType detectBismuthFluidBlending(FogType fogType) {
        if(fogType == FogType.LAVA
                && (BiomeColorMappings.isFluidFogCustomColored(Fluids.LAVA)
                || VanadiumColormaticResolution.hasCustomUnderLavaColors())) {
            return FogType.WATER;
        }

        return fogType;
    }

    @Redirect(
            method="method_24873",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;getFogColor()I"
            )
    )
    private static int customFogColorProxy(Biome self, ClientLevel level, BiomeManager manager, float angleDelta, int x, int y, int z) {
        if(Vanadium.configuration.shouldClearSky
                && level.dimensionType().hasSkyLight()) {
            return self.getFogColor();
        }

        var dimensionId = Vanadium.getDimensionId(level);
        VanadiumResolver resolver = BiomeColorMappings.getTotalSkyFog(dimensionId);
        var coordinates = new Coordinates(
                QuartPos.toBlock(x),
                QuartPos.toBlock(y),
                QuartPos.toBlock(z)
        );
        return resolver.getColorAtCoordinatesForBiome(level.registryAccess(), self, coordinates);
    }
}
