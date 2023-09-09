package vanadium.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.customcolors.VanadiumExtendedColorResolver;
import vanadium.defaults.DefaultColumns;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "setLevel",
    at = @At("HEAD"))
    private void propagateDynamicRegistry(@Nullable ClientLevel world, CallbackInfo ci) {
        var manager = world == null
                ? null
                : world.registryAccess();

        VanadiumExtendedColorResolver.setRegistryManager(manager);
        DefaultColumns.reloadDefaultColumnBoundaries(manager);
    }
}
