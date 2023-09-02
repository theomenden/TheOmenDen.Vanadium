package vanadium.models;

import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class ItemsGrid {
    public List<Identifier> items = Collections.emptyList();
    public int borderColor = -1;
    public int borderWidth = 1;
    public int highlightColor = -1;
    public boolean isGradientBorder = false;
}
