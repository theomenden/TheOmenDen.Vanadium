package vanadium.mixin.coloring.model;

import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.VanadiumColormaticResolution;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
public abstract class BakedModelManagerMixin {

    @Inject(
            method="reload",
            at = @At("HEAD")
    )
    private void reloadVanadiumCustomBiomeColors(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        VanadiumColormaticResolution.CUSTOM_BLOCK_COLORS.onResourceManagerReload(resourceManager);
        VanadiumColormaticResolution.COLORMATIC_CUSTOM_BLOCK_COLORS.onResourceManagerReload(resourceManager);
    }
}
