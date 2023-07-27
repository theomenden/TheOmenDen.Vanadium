package vanadium.mixin.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.properties.DefaultColumns;
import vanadium.resolvers.ExtendedColorResolver;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Inject(method="setLevel", at=@At("HEAD"))
    private void propagateDynamicRegistry(@Nullable ClientLevel level, CallbackInfo ci) {
        var manager = level == null
                ? null
                : level.registryAccess();

        ExtendedColorResolver.setRegistryManager(manager);
        DefaultColumns.reloadDefaultColumnBoundaries(manager);
    }
}
