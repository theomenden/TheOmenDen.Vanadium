package vanadium.mixin.fabricapi;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vanadium.fluids.VanadiumFluidRenderHandler;

@Mixin(value = FluidRenderHandlerRegistryImpl.class, remap = false)
public abstract class FluidRendererHandlerRegistryMixin {
    @ModifyVariable(method = "register", at = @At(value = "HEAD"), ordinal = 0)
    private FluidRenderHandler wrapVanadiumFluidRenderHandler(FluidRenderHandler delegate) {
        return new VanadiumFluidRenderHandler(delegate);
    }
}
