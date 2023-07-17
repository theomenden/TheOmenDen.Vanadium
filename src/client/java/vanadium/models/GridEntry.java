package vanadium.models;

import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class GridEntry {
    public List<Identifier> biomes = Collections.emptyList();
    public int column = -1;
    public int width = 1;
}
