package vanadium.mixin.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;
import vanadium.customcolors.VanadiumExtendedColorResolver;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.models.records.Coordinates;

import java.util.function.Supplier;


@Mixin(ClientLevel.class)
public abstract class ClientWorldMixin extends Level {
    @Final
    @Shadow
    private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = new Object2ObjectArrayMap<>();

    protected ClientWorldMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Shadow
    public abstract int calculateBlockTint(BlockPos pos, ColorResolver colorResolver);



    @Inject(
            method = "getBlockTint",
            at = @At("HEAD")
    )
    private void onGetBlockTint(BlockPos blockPos, ColorResolver colorResolver, CallbackInfoReturnable<Integer> cir) {
        if(this.tintCaches.get(colorResolver) == null) {
            this.tintCaches.put(colorResolver, new BlockTintCache(pos1 -> this.calculateBlockTint(pos1, colorResolver)));
        }
    }
    @Inject(method = "clearTintCaches", at = @At("HEAD"))
    public void onClearColorCaches(CallbackInfo ci) {
        this.tintCaches.entrySet().removeIf(entry -> entry.getKey() instanceof VanadiumExtendedColorResolver);
    }

    @ModifyArg(
            method = "getSkyColor",
            at = @At(
                    value="INVOKE",
                    target="Lnet/minecraft/util/CubicSampler;gaussianSampleVec3(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/util/CubicSampler$Vec3Fetcher;)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 1
    )
    private CubicSampler.Vec3Fetcher proxySkyColor(CubicSampler.Vec3Fetcher fetcher){
        var dimensionId = Vanadium.getDimensionId(this);
        var resolver = BiomeColorMappings.getTotalSky(dimensionId);
        var biomeAccess = this.getBiomeManager();
        var manager = this.registryAccess();

        return (x,y,z) -> {
            var biomeRegistry = manager.registryOrThrow(Registries.BIOME);
            var biome = Vanadium.getRegistryValue(biomeRegistry, biomeAccess.getNoiseBiomeAtQuart(x,y,z));
            var blockCoordinates = new Coordinates( QuartPos.toBlock(x), QuartPos.toBlock(y), QuartPos.toBlock(z));
            return Vec3
                    .fromRGB24(resolver.getColorAtCoordinatesForBiome(manager, biome,blockCoordinates));
        };
    }
}

