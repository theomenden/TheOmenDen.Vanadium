package vanadium.models.enums;

import net.minecraft.util.StringRepresentable;

public enum InternalEventType implements StringRepresentable {
    COLOR("Color Generation"),
    SUBEVENT("Sub Event");

    InternalEventType(String name) {
        this.name = name;
    }

    private final String name;


    @Override
    public String getSerializedName() {
        return this.name;
    }
}
