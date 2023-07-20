package vanadium.particle;

import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;
import vanadium.Vanadium;

public class CutomizedRedDustParticleEffect extends DustParticleEffect {
    private final float oneOver255 = 1/255.0f;
    public CutomizedRedDustParticleEffect(Vector3f color, float alpha) {
        super(color, alpha);
    }

    @Override
    public Vector3f getColor() {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            int fullPoweredColor = getFullPoweredColor();

            float red = ((fullPoweredColor >> 16) & 0xff) * oneOver255;
            float green = ((fullPoweredColor >> 8) & 0xff) * oneOver255;
            float blue = (fullPoweredColor & 0xff) * oneOver255;

            return new Vector3f(red, green, blue);
        }
        return super.getColor();
    }

    @Override
    public float getScale() {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            return ((getFullPoweredColor() >> 24) & 0xff) * oneOver255;
        }
        return super.getScale();
    }

    private int getFullPoweredColor() {
        return Vanadium.REDSTONE_COLORS.getColorAtIndex(15);
    }
}
