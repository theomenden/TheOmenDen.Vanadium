package vanadium.mixin.compatibility;

import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.Vanadium;

import java.util.concurrent.Executor;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
    @Dynamic("reload lambda method")
    @Inject(method = "method_18167", at = @At("HEAD"))
    private void onReloading(ResourceManager manager, Executor exec, Void v, CallbackInfo ci) {
        Vanadium.COLOR_PROPERTIES.reload(manager);
    }
}
