package vanadium.models;

import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class GridEntry {
    public List<Identifier> biomes = Lists.newArrayList();
    public int column = -1;
    public int width = 1;
}
