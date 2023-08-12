package vanadium.customcolors.particle;


import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;
import vanadium.VanadiumClient;

public class CustomRedDustParticle extends DustParticleEffect {
    public CustomRedDustParticle(Vector3f color, float alpha) {
        super(color, alpha);
    }

    @Override
    public Vector3f getColor() {
        return super.getColor();
    }

    @Override
    public float getScale() {
        return super.getScale();
    }

    private int getFullPoweredColor() {
        return VanadiumClient.REDSTONE_COLORS.getColorBounded(15);
    }
}
