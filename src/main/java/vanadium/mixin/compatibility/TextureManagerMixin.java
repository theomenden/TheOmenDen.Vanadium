package vanadium.mixin.compatibility;

import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {

    @Dynamic("Post reload lambda method")
    @Inject(method = "method_18167", at = @At("HEAD"))
    public void reloadVanadiumColorProperties(ResourceManager resourceManager, Executor executor, CompletableFuture completableFuture, Void v, CallbackInfo ci) {
        Vanadium.COLOR_PROPERTIES.reload(resourceManager);
    }
}
