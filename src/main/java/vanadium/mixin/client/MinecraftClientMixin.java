package vanadium.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.customcolors.ExtendedColorResolver;
import vanadium.defaults.DefaultColumns;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setWorld",
    at = @At("HEAD"))
    private void propagateDynamicRegistry(@Nullable ClientWorld world, CallbackInfo ci) {
        var manager = world == null
                ? null
                : world.getRegistryManager();

        ExtendedColorResolver.setRegistryManager(manager);
        DefaultColumns.reloadDefaultColumnBoundaries(manager);
    }
}
