package chromatiq.properties;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class ApplicableBlockStates {
    public Block defaultBlock = Blocks.AIR;
    public Collection<BlockState> states = new ArrayList<>();
    public Identifier specialKey = null;
    public Collection<Identifier> specialIds = new ArrayList<>();
}
