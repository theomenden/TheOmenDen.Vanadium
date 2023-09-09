package vanadium.mixin.compatibility;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ResourceLocation.class)
public abstract class IdentifierMixin {
    @ModifyArg(method="<init>(Ljava/lang/String;Ljava/lang/String;)V",
    index = 0,
    at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/resources/ResourceLocation;assertValidPath(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
    ))
    private static String skipValidationForVanadium(String namespace, String path) {
        return checkForOptifineNamespace(namespace, path)
                ? "safe_id_for_allowing_invalid_chars"
                : path;
    }

    @Unique
    private static boolean checkForOptifineNamespace(String namespace, String path) {
        return namespace.equals("minecraft")
                && path.startsWith("optfine/");
    }
}
