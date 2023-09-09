package vanadium.mixin.compatibility;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.utils.VanadiumColormaticResolution;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

    @Dynamic("Post reload lambda method")
    @Inject(method = "method_18167", at = @At("HEAD"))
    public void reloadVanadiumColorProperties(ResourceManager resourceManager, Executor executor, CompletableFuture completableFuture, Void v, CallbackInfo ci) {
        VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES.onResourceManagerReload(resourceManager);
        VanadiumColormaticResolution.COLOR_PROPERTIES.onResourceManagerReload(resourceManager);
    }
}
