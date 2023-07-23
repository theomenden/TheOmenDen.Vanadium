package vanadium.mixin.sodium;

import me.jellysquid.mods.sodium.client.model.quad.blender.ColorBlender;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vanadium.blending.ConfigurableColorBlender;

@Mixin(BlockRenderCache.class)
public class BlockRenderCacheMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("sodium-blendingregistry");

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "<init>", at = @At("STORE"))
    private ColorBlender injectConfiguredColorBlender(ColorBlender colorBlender) {
        LOGGER.info("Replacing sodium color blender with ConfigurableColorBlender");
        return new ConfigurableColorBlender(Minecraft.getInstance().options);
    }
}
