package vanadium.enums;

import net.minecraft.util.StringIdentifiable;

public enum ColumnLayout implements StringIdentifiable {
    DEFAULT("default"),
    OPTIFINE("optifine"),
    LEGACY("legacy"),
    STABLE("stable");

    private final String name;

    ColumnLayout(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return name;
    }
}
