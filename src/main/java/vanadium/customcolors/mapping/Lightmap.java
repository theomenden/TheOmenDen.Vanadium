package vanadium.customcolors.mapping;

import com.mojang.blaze3d.platform.NativeImage;
import vanadium.Vanadium;

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

    public int getSkyLighting(int level, float ambience, float nightVision) {
        if(ambience < 0) {
            int posX = lightmap.getWidth() - 1;
            return getPixelAtPositionWithGivenLightLevel(posX, level, nightVision);
        }

        float scaledAmbience = ambience * (lightmap.getWidth() - 2);
        float scaledAmbienceModulated = scaledAmbience % 1.0f;

        int posX = (int)scaledAmbience;
        int light = getPixelAtPositionWithGivenLightLevel(posX, level, nightVision);
        boolean shouldBlendSkyLighting = Vanadium.configuration.shouldBlendSkyLight;

        if(shouldBlendSkyLighting
        && posX < lightmap.getWidth() - 2) {
            int rightLighting = getPixelAtPositionWithGivenLightLevel(posX + 1, level, nightVision);
            light = mergeColorsBasedOnNightVisionFactors(rightLighting, light, scaledAmbienceModulated);
        }
        return light;
    }

    private int getPixelAtPositionWithGivenLightLevel(int x, int y, float nightVision) {
        if(nightVision <=0.0f) {
            return lightmap.getPixelRGBA(x, y);
        }
        if(nightVision >=1.0f) {
            if (lightmap.getHeight() != 64) {
                return getRationalizedValue(x, y);
            }  else {
                return lightmap.getPixelRGBA(x, y +32);
            }
        }
        int normalColor = lightmap.getPixelRGBA(x, y);
        int nightVisionColor = (lightmap.getHeight() !=64)
                ? getRationalizedValue(x, y)
                : lightmap.getPixelRGBA(x, y +32);
        return mergeColorsBasedOnNightVisionFactors(normalColor, nightVisionColor, nightVision);
    }

    private int getRationalizedValue(int x, int y) {
        int color = lightmap.getPixelRGBA(x, y);
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
        float oneMinusAweight = 1 - nightVision;
        int cha, chb;
        int resolvedColor = 0xff000000;
        cha = ((color1 >> 16) & 0xff);
        chb = ((color2 >> 16) & 0xff);
        resolvedColor |= (int)(cha * nightVision + chb * oneMinusAweight) << 16;
        cha = ((color1 >> 8) & 0xff);
        chb = ((color2>> 8) & 0xff);
        resolvedColor |= (int)(cha * nightVision + chb * oneMinusAweight) << 8;
        cha = color1 & 0xff;
        chb = color2 & 0xff;
        resolvedColor |= (int)(cha * nightVision + chb * oneMinusAweight);
        return resolvedColor;
    }
}
