package vanadium.resources;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Set;

public class DefaultBlockColorSettings {
    private static final TagKey<Block> MODDED_BLENDED_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "blendable"));
    private static final TagKey<Fluid>  MODDED_BLENDED_FLUIDS = TagKey.of(RegistryKeys.FLUID, new Identifier("c", "blendable"));

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
                 || block.getDefaultState().isIn(MODDED_BLENDED_BLOCKS);
    }

    public static boolean isSmoothBlendingAvailable(Fluid fluid) {
        return BLENDED_FLUIDS.contains(fluid)
                || fluid.getDefaultState().isIn(MODDED_BLENDED_FLUIDS);
    }

    public static void registerForBlending(Block block) {
        BLENDED_BLOCKS.add(block);
    }

    public static void registerForBlending(Fluid fluid) {
        BLENDED_FLUIDS.add(fluid);
    }
}
