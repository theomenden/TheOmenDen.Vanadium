package vanadium.mixin.client;

import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vanadium.blending.ConfigurableColorBlender;

@Mixin(BlockRenderCache.class)
public class BlockRenderCacheMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("vanadium");

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "<init>", at = @At("STORE"))
    private ColorBlender injectConfigurableColorBlender(ColorBlender value) {
        LOGGER.info("Changed sodium default color blender with ConfigurableColorBlender");
        return new ConfigurableColorBlender(MinecraftClient.getInstance());
    }
}
