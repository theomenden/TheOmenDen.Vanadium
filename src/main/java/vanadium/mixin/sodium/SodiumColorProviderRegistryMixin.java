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
import vanadium.customcolors.VanadiumExtendedColorResolver;
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

        var extendedResolver = DefaultVanadiumResolverProviders.BLOCK_PROVIDER;
        registerBlocks(
                VanadiumBlockStateColorProvider.adaptVanadiumColorProvider(new VanadiumExtendedColorResolver((DefaultVanadiumResolverProviders.BLOCK_PROVIDER.create(Blocks.GRASS)))),
                Blocks.GRASS_BLOCK,
                Blocks.FERN,
                Blocks.GRASS,
                Blocks.POTTED_FERN,
                Blocks.PINK_PETALS,
                Blocks.SUGAR_CANE,
                Blocks.LARGE_FERN,
                Blocks.TALL_GRASS
        );

        registerBlocks(
                VanadiumBlockStateColorProvider.adaptVanadiumColorProvider(new VanadiumExtendedColorResolver(DefaultVanadiumResolverProviders.BLOCK_PROVIDER.create(Blocks.OAK_LEAVES))),
                        Blocks.OAK_LEAVES
        );

        registerBlocks(VanadiumBlockStateColorProvider.adaptVanadiumColorProvider(new VanadiumExtendedColorResolver(DefaultVanadiumResolverProviders.BLOCK_PROVIDER.create(Blocks.ACACIA_LEAVES))),
                Blocks.ACACIA_LEAVES);

        registerBlocks(
                VanadiumBlockStateColorProvider.adaptVanadiumColorProvider(new VanadiumExtendedColorResolver(DefaultVanadiumResolverProviders.BLOCK_PROVIDER.create(Blocks.WATER))),
                Blocks.WATER_CAULDRON, Blocks.BUBBLE_COLUMN
        );

        registerBlocks(
                VanadiumBlockStateColorProvider.adaptVanadiumColorProvider(new VanadiumExtendedColorResolver(DefaultVanadiumResolverProviders.BLOCK_PROVIDER.create(Blocks.LAVA))),
                Blocks.LAVA, Blocks.LAVA_CAULDRON, Blocks.FIRE, Blocks.MAGMA_BLOCK
        );

        registerFluids(
                VanadiumFluidStateColorProvider.adaptVanadiumColorProvider(DefaultVanadiumResolverProviders.FLUID_PROVIDER.create(Fluids.LAVA)),
                Fluids.LAVA, Fluids.FLOWING_LAVA
        );

        registerFluids(
                VanadiumFluidStateColorProvider.adaptVanadiumColorProvider(DefaultVanadiumResolverProviders.FLUID_PROVIDER.create(Fluids.WATER)),
                Fluids.WATER, Fluids.FLOWING_WATER);

    }

}