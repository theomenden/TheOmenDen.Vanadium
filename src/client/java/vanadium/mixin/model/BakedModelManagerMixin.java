package vanadium.mixin.model;

import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.util.perf.Profiler;
import vanadium.Vanadium;

@Mixin(ModelManager.class)
public abstract class BakedModelManagerMixin {
    @Inject(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;startTick()V",
                    shift = At.Shift.AFTER
            )
    )
    private void reloadVanadiumCustomBiomeColors(RegistryAccess.ImmutableRegistryAccess manger, Profiler profiler, CallbackInfo ci) {
        Vanadium.CUSTOM_BLOCK_COLORS.onResourceManagerReload(manager);
    }
}
