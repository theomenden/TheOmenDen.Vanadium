package vanadium.enums;

import net.minecraft.util.StringRepresentable;

public enum ColoredParticle implements StringRepresentable {
    WATER("water"),
    LAVA("lava"),
    PORTAL("portal");

    private final String name;

    private ColoredParticle(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
