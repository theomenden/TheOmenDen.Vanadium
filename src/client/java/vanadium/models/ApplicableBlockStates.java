package vanadium.models;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import java.util.Collection;

public final class ApplicableBlockStates {
    public Block block = Blocks.AIR;
    public Collection<BlockState> states = Lists.newArrayList();
    public Identifier specialKey = null;
    public Collection<Identifier> specialIds = Lists.newArrayList();
}
