package vanadium.colormapping;

import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import vanadium.resolvers.DefaultVanadiumResolverProviders;
import vanadium.resolvers.VanadiumRegistryResolver;
import vanadium.util.ColorMappingStorage;
import net.minecraft.block.Block;

public final class BiomeColorMappings {

    private static final ColorMappingStorage<Block> colorMappingsByBlock = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.BLOCK_PROVIDER);
    private static final ColorMappingStorage<Fluid> colorMappingsByFluidFog = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.FLUID_FOG_PROVIDER);
    private static final ColorMappingStorage<Identifier> skyFogColorMappings = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.SKY_FOG_PROVIDER);
    private static final ColorMappingStorage<Identifier> skyColorMappings = new ColorMappingStorage<>(DefaultVanadiumResolverProviders.SKY_PROVIDER);

    private BiomeColorMappings() {
    }

    public static VanadiumRegistryResolver getTotalSky(Identifier dimensionId) {
        return skyColorMappings.get(dimensionId);
    }
}
