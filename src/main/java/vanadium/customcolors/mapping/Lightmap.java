package vanadium.customcolors.mapping;

import net.minecraft.client.texture.NativeImage;

public class Lightmap {
    private final NativeImage lightmap;

    public Lightmap(NativeImage lightmap) {
        this.lightmap = lightmap;
    }

    public int getBlockColorForLightLevel(int lightLevel, float flicker, float nightVision) {
        int width = lightmap.getWidth();
        int positionX = (int)(flicker * width) % width;

        if(positionX < 0) {
            positionX = -positionX;
        }

        return getPixelAtPositionWithGivenLightLevel(positionX, lightLevel + 16, nightVision);
    }


    private int getPixelAtPositionWithGivenLightLevel(int x, int y, float nightVision) {
        if(nightVision <=0.0f) {
            return lightmap.getColor(x, y);
        }
        if(nightVision >=1.0f) {
            if (lightmap.getHeight() != 64) {
                return getRationalizedValue(x, y);
            }  else {
                return lightmap.getColor(x, y +32);
            }
        }
        int normalColor = lightmap.getColor(x, y);
        int nightVisionColor = (lightmap.getHeight() !=64)
                ? getRationalizedValue(x, y)
                : lightmap.getColor(x, y +32);
        return mergeColorsBasedOnNightVisionFactors(normalColor, nightVisionColor, nightVision);
    }

    private int getRationalizedValue(int x, int y) {
        int color = lightmap.getColor(x, y);
        int red = (color >>16) &0xff;
        int green = (color >>8) &0xff;
        int blue = color & 0xff;
        int scale = Math.max(red, Math.max(green, blue));
        int rationalizedValue;
        if (scale == 0) {
            rationalizedValue = 0x00ffffff;
        } else {
            int inverseScale = 255 / scale;
            rationalizedValue = 0xff000000 | (inverseScale * red) << 16 | (inverseScale * green) << 8 | (inverseScale * blue);
        }
        return rationalizedValue;
    }

    private int mergeColorsBasedOnNightVisionFactors(int color1, int color2, float nightVision) {
        float nightVisionFactor = 1.0f - nightVision;

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
