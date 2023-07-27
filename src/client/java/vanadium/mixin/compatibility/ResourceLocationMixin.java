package vanadium.mixin.compatibility;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin {

    @Shadow public abstract String getPath();

    @Shadow @Final public static String DEFAULT_NAMESPACE;

    @ModifyArg(
            method="<init>(Ljava/lang/String;Ljava/lang/String;)V",
            index = 0,
            at = @At(
                    value = "INVOKE",
                    target ="Lnet/minecraft/resources/ResourceLocation;assertValidPath(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
            )
    )
    private static String skipValidationForVanadium(String namespace, String path) {
        if(namespace.equals(DEFAULT_NAMESPACE)
        && path.startsWith("optifine/")) {
            path = "safe_id_for_allowing_invalid_chars";
        }
        return path;
    }
}
