package vanadium.resolvers;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import vanadium.entry.Vanadium;

public final class DefaultVanadiumResolverProviders {

    public static final VanadiumResolverProvider<BlockState> BLOCK_STATE_PROVIDER = DefaultVanadiumResolverProviders::resolveByBlockState;
    public static final VanadiumResolverProvider<Block> BLOCK_PROVIDER = DefaultVanadiumResolverProviders::resolveByBlock;
    public static final VanadiumResolverProvider<ResourceLocation> SKY_PROVIDER = DefaultVanadiumResolverProviders::resolveBySky;
    public static final VanadiumResolverProvider<ResourceLocation> SKY_FOG_PROVIDER = DefaultVanadiumResolverProviders::resolveByFog;
    public static final VanadiumResolverProvider<Fluid> FLUID_FOG_PROVIDER = key -> (manager, biome, coordinates) -> -1;

    private DefaultVanadiumResolverProviders() {

    }

    private static VanadiumResolver resolveByBlockState(BlockState key) {
        return (manager, biome, coordinates) -> {

            var minecraftInstance = Minecraft.getInstance();

           var colorProvider =  ColorProviderRegistry.BLOCK.get(key.getBlock());


            if(colorProvider != null) {
                var world = minecraftInstance.level;


                Vec3i pos = new Vec3i(coordinates.x(), coordinates.y(), coordinates.z());
                return colorProvider.getColor(key,
                        world,
                        new BlockPos(pos),
                        0);
            }
            return -1;
        };
    }
    private static VanadiumResolver resolveByBlock(Block key) {
        return resolveByBlockState(key.defaultBlockState());
    }

    private static VanadiumResolver resolveBySky(ResourceLocation key) {
        return (manager, biome, coordinates) -> {
           int color = 0;

           if(Vanadium.SKY_COLORS.hasCustomColorMapping()
           && key.equals(Vanadium.OVERWORLD_ID)) {
               color = Vanadium.SKY_COLORS.getColorMapping().getColorAtCoordinatesForBiomeByManager(manager, biome, coordinates);
           } else {
               color = Vanadium.COLOR_PROPERTIES.getProperties().getDimensionSky(key);

               if(color == 0) {
                   color = biome.getSkyColor();
               }
           }
            return color;
        };
    }

    private static VanadiumResolver resolveByFog(ResourceLocation key) {
        return (manager, biome, coordinates) -> {
            int color = 0;

            if(Vanadium.FOG_COLORS.hasCustomColorMapping()
            && key.equals(Vanadium.OVERWORLD_ID)) {
                color = 0xff000000 | Vanadium.FOG_COLORS.getColorMapping().getColorAtCoordinatesForBiomeByManager(manager, biome, coordinates);
            } else {
                color = Vanadium.COLOR_PROPERTIES.getProperties().getDimensionFog(key);

                if(color == 0) {
                    color = biome.getFogColor();
                }
            }
            return color;
        };
    }
}
