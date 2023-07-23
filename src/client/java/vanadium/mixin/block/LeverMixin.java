package vanadium.mixin.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeverBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LeverBlock.class)
public abstract class LeverMixin extends Block {
    private LeverMixin() {
        super(null);
    }


}
