package vanadium.particle;


import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustParticle;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import vanadium.Vanadium;
import vanadium.util.MathUtils;

public class CutomizedRedDustParticleEffect extends DustParticle {
    public CutomizedRedDustParticleEffect(ClientLevel level, Vector3f color, float alpha) {

        super(level, color.x(), color.y(),color.z());
    }

    @Override
    public Vector3f getColor() {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            int fullPoweredColor = getFullPoweredColor();

            float red = ((fullPoweredColor >> 16) & 0xff) * MathUtils.INV_255;
            float green = ((fullPoweredColor >> 8) & 0xff) * MathUtils.INV_255;
            float blue = (fullPoweredColor & 0xff) * MathUtils.INV_255;

            return new Vector3f(red, green, blue);
        }
        return super.;
    }

    @Override
    public float getScale() {
        if(Vanadium.REDSTONE_COLORS.hasCustomColorMapping()) {
            return ((getFullPoweredColor() >> 24) & 0xff) * MathUtils.INV_255;
        }
        return super.scale;
    }

    private int getFullPoweredColor() {
        return Vanadium.REDSTONE_COLORS.getColorAtIndex(15);
    }
}
