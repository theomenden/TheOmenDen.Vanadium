package vanadium.mixin.sodium;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import me.jellysquid.mods.sodium.client.model.color.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
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
public abstract class SodiumColorProviderRegistryMixin  {
    @Final
    @Shadow
    private Reference2ReferenceMap<Block, ColorProvider<BlockState>> blocks;

    @Shadow protected abstract void registerBlocks(ColorProvider<BlockState> resolver, Block... blocks);


    @Shadow @Final private Reference2ReferenceMap<Fluid, ColorProvider<FluidState>> fluids;

    @Shadow protected abstract void registerFluids(ColorProvider<FluidState> resolver, Fluid... fluids);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(BlockColors blockColors, CallbackInfo ci) {
        blocks.keySet()
                      .forEach(block -> {
                         var provider = VanadiumBlockStateColorProvider.adaptVanadiumColorProvider(DefaultVanadiumResolverProviders.BLOCK_STATE_PROVIDER.create(block.getDefaultState()));
                         this.registerBlocks(provider, block);
                      });

        fluids.keySet()
                .forEach(fluid -> {
                    var provider = VanadiumFluidStateColorProvider.adaptVanadiumColorProvider(DefaultVanadiumResolverProviders.FLUID_STATE_PROVIDER.create(fluid.getDefaultState()));
                    this.registerFluids(provider, fluid);
                });
    }
}