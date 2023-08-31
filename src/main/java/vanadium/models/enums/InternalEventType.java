package vanadium.models.enums;

import net.minecraft.util.StringIdentifiable;

public enum InternalEventType implements StringIdentifiable {
    COLOR("Color Generation"),
    SUBEVENT("Sub Event");

    InternalEventType(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String asString() {
        return this.name();
    }
}
