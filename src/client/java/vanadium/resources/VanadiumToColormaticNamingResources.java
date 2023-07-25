package vanadium.resources;

import net.minecraft.resources.ResourceLocation;

public final class VanadiumToColormaticNamingResources {
    private static final String COLORMATIC_ID = "colormatic";
    private static final String VANADIUM_ID = "vanadium";

    /**
     * Changes colormatic instances with vanadium to maintain compatibility
     * @param resourceLocation the provided colormatic location
     * @return resourceLocation where the namespace has been changed to vanadium's.
     */
    public static ResourceLocation changeColormaticToVanadium(ResourceLocation resourceLocation)  {
        if(resourceLocation.getNamespace().equals(COLORMATIC_ID)) {
            return ResourceLocation.tryBuild(VANADIUM_ID, resourceLocation.getPath());
        }
        return resourceLocation;
    }
}
