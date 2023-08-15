package vanadium.mixin.coloring.particle;

import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;
import vanadium.customcolors.mapping.BiomeColorMapping;
import vanadium.utils.MathUtils;

@Mixin(WaterSplashParticle.class)
public abstract class WaterSplashParticleMixin extends SpriteBillboardParticle {

    @Unique
    private static final BlockPos.Mutable position = new BlockPos.Mutable();
    private WaterSplashParticleMixin() {
        super(null, 0.0, 0.0, 0.0);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstructor(CallbackInfo ci) {
        if(!Vanadium.WATER_COLORS.hasCustomColorMapping()) {
            return;
        }

        BiomeColorMapping colorMapping = Vanadium.WATER_COLORS.getColorMapping();

        position.set(this.x, this.y, this.z);

        int color = BiomeColorMapping.getBiomeCurrentColorOrDefault(this.world, position, colorMapping);

        this.red = ((color >> 16) & 0xff) * MathUtils.INV_255;
        this.green = ((color >> 8) & 0xff) * MathUtils.INV_255 ;
        this.blue = (color & 0xff) * MathUtils.INV_255;
    }
}
