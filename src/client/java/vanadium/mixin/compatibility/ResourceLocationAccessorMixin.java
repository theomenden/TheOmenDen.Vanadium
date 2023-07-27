package vanadium.mixin.compatibility;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ResourceLocation.class)
public interface ResourceLocationAccessorMixin {
    @Invoker
    boolean callIsValidPath(String path);
}
