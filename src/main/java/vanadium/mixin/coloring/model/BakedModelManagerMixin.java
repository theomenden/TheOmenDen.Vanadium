package vanadium.mixin.coloring.model;

import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.VanadiumColormaticResolution;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {

    @Inject(
            method="reloadModels",
            at = @At("HEAD")
    )
    private static void reloadVanadiumCustomBiomeColors(ResourceManager resourceManager, Executor executor, CallbackInfoReturnable<CompletableFuture<Map<Identifier, JsonUnbakedModel>>> cir) {
        VanadiumColormaticResolution.CUSTOM_BLOCK_COLORS.reload(resourceManager);
        VanadiumColormaticResolution.COLORMATIC_CUSTOM_BLOCK_COLORS.reload(resourceManager);
    }
}
