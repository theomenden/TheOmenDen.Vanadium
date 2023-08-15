package vanadium.models.enums;

import net.minecraft.util.StringIdentifiable;

public enum ColoredParticle implements StringIdentifiable {
    WATER("water"),
    LAVA("lava"),
    PORTAL("portal");

    private final String name;

    private ColoredParticle(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
