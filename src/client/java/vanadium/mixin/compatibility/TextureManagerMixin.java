package vanadium.mixin.compatibility;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.entry.Vanadium;

import java.util.concurrent.Executor;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
    @Dynamic("Post reload lambda method")
    @Inject(method = "method_18167", at=@At("HEAD"))
    private void onReload(ResourceManager manager, Executor exec, Void v, CallbackInfo info) {
        Vanadium.COLOR_PROPERTIES.onResourceManagerReload(manager);
    }
}
