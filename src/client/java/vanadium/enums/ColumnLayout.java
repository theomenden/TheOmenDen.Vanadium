package vanadium.enums;

import net.minecraft.util.StringRepresentable;

public enum ColumnLayout implements StringRepresentable {
    DEFAULT("default"),
    OPTIFINE("optifine"),
    LEGACY("legacy"),
    STABLE("stable");

    private final String name;

    ColumnLayout(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
