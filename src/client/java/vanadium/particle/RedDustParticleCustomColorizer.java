package vanadium.particle;

import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;

public class RedDustParticleCustomColorizer extends DustParticleEffect {
    public RedDustParticleCustomColorizer(Vector3f color, float alpha) {
        super(color, alpha);
    }

    @Override
    public Vector3f getColor() {
        if(Chromatiq.REDSTONE_COLOR_MAPPINGS.hasCustomColors()) {
            int rgbIndex = getFullColorIndex();
            float redAmount = ((rgbIndex >> 16) & 0xff) / 255.0f;
            float greenAmount = ((rgbIndex >> 8) & 0xff) / 255.0f;
            float blueAmount = (rgbIndex & 0xff) / 255.0f;

            return new Vector3f(redAmount, greenAmount, blueAmount);
        }
        return super.getColor();
    }

    @Override
    public float getScale() {
        if(Chromatiq.REDSTONE_COLOR_MAPPINGS.hasCustomColors()) {
            return ((getFullColorIndex() >> 24) & 0xff) / 255.0f;
        }
        return super.getScale();
    }

    private int getFullColorIndex() {
        return Chromatiq.REDSTONE_COLOR_MAPPINGS.getboundColorIndex(15);
    }
}
