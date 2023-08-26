package vanadium.mixin.sodium;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.color.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.customcolors.VanadiumBlockStateColorProvider;
import vanadium.customcolors.VanadiumFluidStateColorProvider;
import vanadium.defaults.DefaultVanadiumResolverProviders;

@Mixin(ColorProviderRegistry.class)
public abstract class SodiumColorProviderRegistryMixin {
    @Final
    @Shadow
    private Reference2ReferenceMap<Block, ColorProvider<BlockState>> blocks;

    @Shadow
    protected abstract void registerBlocks(ColorProvider<BlockState> resolver, Block... blocks);

    @Shadow
    @Final
    private Reference2ReferenceMap<Fluid, ColorProvider<FluidState>> fluids;

    @Shadow
    protected abstract void registerFluids(ColorProvider<FluidState> resolver, Fluid... fluids);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(BlockColors blockColors, CallbackInfo ci) {
        ColorProvider<BlockState> lavaBlockProvider = VanadiumBlockStateColorProvider.adaptVanadiumColorProvider(DefaultVanadiumResolverProviders.BLOCK_PROVIDER.create(Blocks.LAVA));
        ColorProvider<FluidState> lavaFluidProvider = VanadiumFluidStateColorProvider.adaptVanadiumColorProvider(DefaultVanadiumResolverProviders.FLUID_PROVIDER.create(Fluids.LAVA));
        this.registerBlocks(lavaBlockProvider, Blocks.LAVA, Blocks.LAVA_CAULDRON);
        this.registerFluids(lavaFluidProvider, Fluids.LAVA, Fluids.FLOWING_LAVA);

        blocks.keySet()
                .stream()
              .filter(b -> !(b.equals(Blocks.LAVA) || b.equals(Blocks.LAVA_CAULDRON)))
                .forEach(block -> {
                    var provider = VanadiumBlockStateColorProvider.adaptVanadiumColorProvider(DefaultVanadiumResolverProviders.BLOCK_PROVIDER.create(block));
                    this.registerBlocks(provider, block);
                });


        fluids.keySet()
              .stream()
              .filter(f -> !(f.equals(Fluids.LAVA) || f.equals(Fluids.FLOWING_LAVA)))
                .forEach(fluid -> {
                    var provider = VanadiumFluidStateColorProvider.adaptVanadiumColorProvider(DefaultVanadiumResolverProviders.FLUID_PROVIDER.create(fluid));
                            this.registerFluids(provider, fluid);
                });
    }

}