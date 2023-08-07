package vanadium.particle;


import net.minecraft.core.particles.DustParticleOptions;
import org.joml.Vector3f;
import vanadium.entry.Vanadium;
import vanadium.util.ColorConverter;
import vanadium.util.MathUtils;

public class CustomizedRedDustParticleEffect extends DustParticleOptions {
    public CustomizedRedDustParticleEffect(Vector3f color, float alpha) {
        super(color, alpha);
    }

    @Override
    public float getScale() {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            return((getFullPoweredColor() >> 24) & 0xff) * MathUtils.INV_255;
        }
        return super.getScale();
    }

    @Override
    public Vector3f getColor() {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            int fullPoweredColor = getFullPoweredColor();
            return ColorConverter.createColorVector(fullPoweredColor);
        }
        return DustParticleOptions.REDSTONE.getColor();
    }

    private int getFullPoweredColor() {
        return Vanadium.REDSTONE_COLORS.getColorAtIndex(15);
    }
}
