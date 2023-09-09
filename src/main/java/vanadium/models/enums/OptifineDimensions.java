package vanadium.models.enums;

import net.minecraft.util.StringRepresentable;

public enum OptifineDimensions implements StringRepresentable {
    OVERWORLD("world0"),
    NETHER("world-1"),
    END("world1");

    private final String name;

    OptifineDimensions(String name) {
        this.name = name;
    }


    @Override
    public String getSerializedName() {
        return this.name;
    }
}
