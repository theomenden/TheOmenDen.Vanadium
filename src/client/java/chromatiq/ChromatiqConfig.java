package chromatiq;

public record ChromatiqConfig(boolean ShouldClearSky,
                              boolean ShouldClearVoid,
                              boolean ShouldBlendSkyLights,
                              boolean ShouldFlickerBlockLights,
                              double BlockLightIntensityExponent) {

    public static double ScaledToRelativeBlockLightIntensity(double relativeBlockLightIntensityExponent) {
        return Math.log(2) * 0.25 * relativeBlockLightIntensityExponent;
    }

    static final ChromatiqConfig DefaultConfiguration = new ChromatiqConfig(false, false, true, true, -13.0);
}
