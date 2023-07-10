package vanadium.enums;

import net.minecraft.util.StringIdentifiable;

public enum Format implements StringIdentifiable {
    FIXED("fixed"),
    VANILLA("vanilla"),
    GRID("grid");

    private final String name;

    Format(String s) {
        name = s;
    }

    @Override
    public String asString() {
        return name;
    }
}
