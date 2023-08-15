package vanadium.mixin.coloring.model;

import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin {

    @Inject(
            method="reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;startTick()V",
                    shift = At.Shift.AFTER
            )
    )
    private void reloadVanadiumCustomBiomeColors(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        Vanadium.CUSTOM_BLOCK_COLORS.reload(manager);
    }
}
