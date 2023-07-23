package vanadium.resources;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Set;

public class DefaultBlockColorSettings {
    private static final TagKey<Block> MODDED_BLENDED_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation("c", "blendable"));
    private static final TagKey<Fluid>  MODDED_BLENDED_FLUIDS = TagKey.create(Registries.FLUID, new ResourceLocation("c", "blendable"));

    private static final Set<Block> BLENDED_BLOCKS = new ReferenceOpenHashSet<>(Sets.newHashSet(
            Blocks.FERN, Blocks.LARGE_FERN, Blocks.POTTED_FERN, Blocks.GRASS,
            Blocks.TALL_GRASS, Blocks.GRASS_BLOCK, Blocks.OAK_LEAVES, Blocks.JUNGLE_LEAVES,
            Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.BIRCH_LEAVES,  Blocks.MANGROVE_LEAVES,
            Blocks.AZALEA_LEAVES, Blocks.FLOWERING_AZALEA_LEAVES, Blocks.CHERRY_LEAVES, Blocks.VINE, Blocks.WATER, Blocks.BUBBLE_COLUMN,
            Blocks.WATER_CAULDRON, Blocks.SUGAR_CANE
    ));

    private static final Set<Fluid>  BLENDED_FLUIDS = new ReferenceOpenHashSet<>(Sets.newHashSet(
            Fluids.EMPTY,
            Fluids.WATER,
            Fluids.LAVA,
            Fluids.FLOWING_WATER,
            Fluids.FLOWING_LAVA));

    public static boolean isSmoothBlendingAvailable(Block block) {
         return BLENDED_BLOCKS.contains(block)
                 || block.defaultBlockState().is(MODDED_BLENDED_BLOCKS);
    }

    public static boolean isSmoothBlendingAvailable(Fluid fluid) {
        return BLENDED_FLUIDS.contains(fluid)
                || fluid.defaultFluidState().is(MODDED_BLENDED_FLUIDS);
    }

    public static void registerForBlending(Block block) {
        BLENDED_BLOCKS.add(block);
    }

    public static void registerForBlending(Fluid fluid) {
        BLENDED_FLUIDS.add(fluid);
    }
}
