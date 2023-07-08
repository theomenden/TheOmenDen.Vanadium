package chromatiq.lightmapping;

import net.minecraft.client.texture.NativeImage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
public class Lightmap {
    private static final Logger log = LogManager.getLogger(Lightmap.class);

    private final NativeImage _lightmap;

    public Lightmap(NativeImage lightmap) {
        _lightmap = lightmap;
    }

    public int getBlockColorForLightLevel(int lightLevel, float flicker, float nightVision) {
        int width = _lightmap.getWidth();
        int positionX = (int)(flicker * width) % width;

        if(positionX < 0) {
            positionX = -positionX;
        }

        return getPixelAtPositionWithGivenLightLevel(positionX, lightLevel + 16, nightVision);
    }


    private int getPixelAtPositionWithGivenLightLevel(int x, int y, float nightVision) {
        if(nightVision <=0.0f) {
            return _lightmap.getColor(x, y);
        }
        if(nightVision >=1.0f) {
            if (_lightmap.getHeight() !=64) {
                return getRationalizedValue(x, y);
            }  else {
                return _lightmap.getColor(x, y +32);
            }
        }
        int normalColor = _lightmap.getColor(x, y);
        int nightVisionColor = (_lightmap.getHeight() !=64)
                ? getRationalizedValue(x, y)
                : _lightmap.getColor(x, y +32);
        return mergeColorsBasedOnNightVisionFactors(normalColor, nightVisionColor, nightVision);
    }

    private int getRationalizedValue(int x, int y) {
        int color = _lightmap.getColor(x, y);
        int red = (color >>16) &0xff;
        int green = (color >>8) &0xff;
        int blue = color & 0xff;
        int scale = Math.max(red, Math.max(green, blue));
        int rationalizedValue;
        if (scale == 0) {
            rationalizedValue = 0x00ffffff;
        } else {
            int inverseScale =255 / scale;
            rationalizedValue = 0xff000000 | (inverseScale * red) << 16 | (inverseScale * green) << 8 | (inverseScale * blue);
        }
        return rationalizedValue;
    }

    private int mergeColorsBasedOnNightVisionFactors(int color1, int color2, float nightVision) {
        float nightVisionFactor =1.0f - nightVision;

        int tempColor1 = color1 &0xff;
        int tempColor2 = color2 &0xff;
        int resolvedColor = (int)((tempColor1 * nightVision) + (tempColor2 * nightVisionFactor));

        tempColor1 = (color1 >>8) &0xff;
        tempColor2 = (color2 >>8) &0xff;
        resolvedColor |= (int)((tempColor1 * nightVision) + (tempColor2 * nightVisionFactor)) <<8;

        tempColor1 = (color1 >>16) &0xff;
        tempColor2 = (color2 >>16) &0xff;
        resolvedColor |= (int)((tempColor1 * nightVision) + (tempColor2 * nightVisionFactor)) <<16;

        resolvedColor |=0xff000000;

        return resolvedColor;
    }
}
