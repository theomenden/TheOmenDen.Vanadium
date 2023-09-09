package vanadium.customcolors.particle;


import net.minecraft.core.particles.DustParticleOptions;
import org.apache.commons.lang3.ObjectUtils;
import org.joml.Vector3f;
import vanadium.utils.ColorConverter;
import vanadium.utils.MathUtils;
import vanadium.utils.VanadiumColormaticResolution;

public class CustomRedDustParticle extends DustParticleOptions {
    public CustomRedDustParticle(Vector3f color, float alpha) {
        super(color, alpha);
    }

    @Override
    public Vector3f getColor() {
        if(VanadiumColormaticResolution.hasCustomRedstoneColors()) {
            var rgb = getFullPoweredColor();
            return ColorConverter.createColorVector(rgb);
        }
        return super.getColor();
    }

    @Override
    public float getScale() {
        if(VanadiumColormaticResolution.hasCustomRedstoneColors()) {
            return ((getFullPoweredColor() >> 24) & 0xff) * MathUtils.INV_255;
        }
        return super.getScale();
    }

    private int getFullPoweredColor() {
        var redstoneColors = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.REDSTONE_COLORS,
                VanadiumColormaticResolution.COLORMATIC_REDSTONE_COLORS
        );
        return redstoneColors.getColorAtIndex(15);
    }
}
