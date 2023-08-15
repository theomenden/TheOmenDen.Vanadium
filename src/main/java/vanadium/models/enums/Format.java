package vanadium.models.enums;

import net.minecraft.util.StringIdentifiable;

public enum Format implements StringIdentifiable {
    FIXED("fixed"),
    VANILLA("vanilla"),
    GRID("grid");

    private final String name;

    Format(String s) {
        this.name = s;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
