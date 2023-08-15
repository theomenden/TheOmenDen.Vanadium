package vanadium.models.enums;

import net.minecraft.util.StringIdentifiable;

public enum OptifineDimensions implements StringIdentifiable {
    OVERWORLD("world0"),
    NETHER("world-1"),
    END("world1");

    private final String name;

    OptifineDimensions(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
