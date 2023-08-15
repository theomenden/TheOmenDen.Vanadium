package vanadium.mixin.coloring.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vanadium.Vanadium;
import vanadium.utils.MathUtils;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin extends Block {
    private RedstoneWireBlockMixin() {
        super(null);
    }

    @Inject(method="getWireColor",
    at=@At("HEAD"),
    cancellable = true)
    private static void injectWireColor(int power, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Vanadium.REDSTONE_COLORS.getColorAtIndex(power));
    }

    @Inject(
            method = "randomDisplayTick",
            at = @At(
                    value = "FIELD",
                    target= "Lnet/minecraft/util/math/Direction$Type;HORIZONTAL:Lnet/minecraft/util/math/Direction$Type;",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void onRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci, int i) {
        if(!Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            return;
        }

        double x = pos.getX() + 0.5 + (random.nextFloat() - 0.5) * 0.2;
        double y = (float)pos.getY() + 0.0625F;
        double z = pos.getZ() + 0.5 + (random.nextFloat() - 0.5) * 0.2;
        int color = Vanadium.REDSTONE_COLORS.getColorAtIndex(i);

        float red = ((color >> 16) & 0xff) * MathUtils.INV_255;
        float green = ((color >> 8) & 0xff) * MathUtils.INV_255;
        float blue = (color & 0xff) * MathUtils.INV_255;

        world.addParticle(new DustParticleEffect(new Vector3f(red,green,blue), 1.0f), x, y, z, 0.0, 0.0, 0.0);
        ci.cancel();
    }
}
