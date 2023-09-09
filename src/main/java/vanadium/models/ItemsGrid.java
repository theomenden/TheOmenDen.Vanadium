package vanadium.models;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class ItemsGrid {
    public List<ResourceLocation> items = Collections.emptyList();
    public int borderColor = -1;
    public int borderWidth = 1;
    public int highlightColor = -1;
    public boolean isGradientBorder = false;
}
