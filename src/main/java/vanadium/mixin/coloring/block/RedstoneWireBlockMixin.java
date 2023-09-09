package vanadium.mixin.coloring.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import vanadium.customcolors.resources.LinearColorMappingResource;
import vanadium.utils.ColorConverter;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(RedStoneWireBlock.class)
public abstract class RedstoneWireBlockMixin extends Block {
    private RedstoneWireBlockMixin() {
        super(null);
    }

    @Inject(method="getColorForPower",
    at=@At("HEAD"),
    cancellable = true)
    private static void injectWireColor(int power, CallbackInfoReturnable<Integer> cir) {
        if(VanadiumColormaticResolution.hasCustomRedstoneColors()) {
            var redstoneColors = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.REDSTONE_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_REDSTONE_COLORS
            );
            cir.setReturnValue(redstoneColors.getColorAtIndex(power));
        }
    }

    @Inject(
            method = "animateTick",
            at =@At(
                    value = "FIELD",
                    target = "Lnet/minecraft/core/Direction$Plane;HORIZONTAL:Lnet/minecraft/core/Direction$Plane;",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci, int i) {
        if(VanadiumColormaticResolution.hasCustomRedstoneColors()) {
            final LinearColorMappingResource redstoneColor = ObjectUtils.firstNonNull(
                    VanadiumColormaticResolution.REDSTONE_COLORS,
                    VanadiumColormaticResolution.COLORMATIC_REDSTONE_COLORS
            );

            double x = pos.getX() + 0.5 + (random.nextFloat() - 0.5) * 0.2;
            double y = ((float)pos.getY() + 0.0625F);
            double z = pos.getZ() + 0.5 + (random.nextFloat() - 0.5) * 0.2;

            int color = redstoneColor.getColorAtIndex(i);

            var colorValues = ColorConverter.createColorVector(color);

            level.addParticle(new DustParticleOptions(colorValues, 1.0f), x, y, z, 0.0, 0.0, 0.0);
            ci.cancel();
        }
    }
}
