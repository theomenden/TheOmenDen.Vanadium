package vanadium.customcolors.particle;


import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;
import vanadium.Vanadium;
import vanadium.utils.ColorConverter;
import vanadium.utils.MathUtils;

public class CustomRedDustParticle extends DustParticleEffect {
    public CustomRedDustParticle(Vector3f color, float alpha) {
        super(color, alpha);
    }

    @Override
    public Vector3f getColor() {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            var rgb = getFullPoweredColor();
            return ColorConverter.createColorVector(rgb);
        }
        return super.getColor();
    }

    @Override
    public float getScale() {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            return ((getFullPoweredColor() >> 24) & 0xff) * MathUtils.INV_255;
        }
        return super.getScale();
    }

    private int getFullPoweredColor() {
        return Vanadium.REDSTONE_COLORS.getColorAtIndex(15);
    }
}
