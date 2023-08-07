package vanadium.enums;

import net.minecraft.util.StringRepresentable;

public enum OptifineDimensions implements StringRepresentable {
    OVERWORLD {
        @Override
        public String getSerializedName() {
            return "world0";
        }
    },
    NETHER {
        @Override
        public String getSerializedName() {
            return "world-1";
        }
    },
    END {
        @Override
        public String getSerializedName() {
            return "world1";
        }
    };


    @Override
    public String getSerializedName() {
        return this.name();
    }
}
