package vanadium.mixin.compatibility;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Identifier.class)
public abstract class IdentifierMixin {
    @ModifyArg(method="<init>(Ljava/lang/String;Ljava/lang/String;)V",
    index = 0,
    at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/Identifier;validatePath(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
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
