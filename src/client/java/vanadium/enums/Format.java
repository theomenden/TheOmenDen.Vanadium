package vanadium.enums;

import net.minecraft.util.StringRepresentable;

public enum Format implements StringRepresentable {
    FIXED("fixed"),
    VANILLA("vanilla"),
    GRID("grid");

    private final String name;

    Format(String s) {
        name = s;
    }
    @Override
    public String getSerializedName() {
        return this.name;
    }
}
