package vanadium.mixin.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vanadium.Vanadium;
import vanadium.util.MathUtils;


@Mixin(RedStoneWireBlock.class)
public abstract class RedstoneWireMixin extends Block {
    @Shadow @Final public static IntegerProperty POWER;

    private RedstoneWireMixin() {
        super(null);
    }

    @Inject(method= "getColorForPower", at = @At("HEAD"), cancellable = true)
    private static void onWireColorChange(int power, CallbackInfoReturnable<Integer> cir){
       if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
           cir.setReturnValue(Vanadium.REDSTONE_COLORS.getColorAtIndex(power));
       }
    }

    @Inject(method="animateTick",
    at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/core/Direction$Plane;iterator()Ljava/util/Iterator;",
            ordinal = 0),
    locals = LocalCapture.CAPTURE_FAILHARD,
    cancellable = true)
    private void onRandomDisplayTick(BlockState state, Level world, BlockPos pos, RandomSource random, CallbackInfo ci, int power) {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            double x = pos.getX() + 0.5 + (random.nextFloat() - 0.5)*0.2;
            double y = ((float)pos.getY() + 0.0625f);
            double z = pos.getZ() + 0.5 + (random.nextFloat() - 0.5)*0.2;
            int color = Vanadium.REDSTONE_COLORS.getColorAtIndex(power);
            float r = ((color >> 16) & 0xff) * MathUtils.INV_255;
            float g = ((color >> 8) & 0xff) * MathUtils.INV_255;
            float b = (color & 0xff) * MathUtils.INV_255;
            world.addParticle(new DustParticleOptions(new Vector3f(r,g,b),1.0f), x,y,z, 0.0,0.0,0.0);
            ci.cancel();
        }
    }
}
