package vanadium.properties;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;

public class ApplicableBlockStates {
    public Block defaultBlock = Blocks.AIR;
    public Collection<BlockState> states = new ArrayList<>();
    public ResourceLocation specialKey = null;
    public Collection<ResourceLocation> specialIds = new ArrayList<>();
}
