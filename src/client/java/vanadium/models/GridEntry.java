package vanadium.models;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class GridEntry {
    public List<ResourceLocation> biomes = Collections.emptyList();
    public int column = -1;
    public int width = 1;
}
