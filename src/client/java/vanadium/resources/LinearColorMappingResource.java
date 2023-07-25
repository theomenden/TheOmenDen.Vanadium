package vanadium.resources;

import net.minecraft.resources.ResourceLocation;

import java.util.random.RandomGenerator;

public class LinearColorMappingResource extends ColorMappingResource {

    public LinearColorMappingResource(ResourceLocation identifier) {
        super(identifier);
    }

    public int getColorAtIndex(int index) {
        if(index >= this.colorMapping.length) {
            index = this.colorMapping.length - 1;
        }
        return this.colorMapping[index];
    }

    public int getColorAtModulatedIndex(int index) {
        index %= this.colorMapping.length;
        return this.colorMapping[index];
    }

    public int getRandomColorFromMapping() {
        RandomGenerator random = RandomGenerator.getDefault();
        return this.colorMapping[random.nextInt(this.colorMapping.length)];
    }

    public int getFractionalColorMapping(float fraction) {
        return this.colorMapping[(int)(fraction * (this.colorMapping.length - 1))];
    }

}
