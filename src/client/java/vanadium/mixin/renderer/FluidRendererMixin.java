package vanadium.mixin.renderer;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.colormapping.BiomeColorMappings;

@Mixin(LiquidBlockRenderer.class)
public class FluidRendererMixin {

    @SuppressWarnings("DataFlowIssue")
    @Redirect(method = "tesselate",
    at = @At(
            value="INVOKE",
            target = "Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;vertex(Lcom/mojang/blaze3d/vertex/VertexConsumer;DDDFFFFFI)V"
    ))
    private void redirectVertextConsumption(
            LiquidBlockRenderer instance, VertexConsumer consumer, double x, double y, double z, float red, float green, float blue, float u, float v, int packedLight,
            BlockAndTintGetter world, BlockPos pos
    ) {
        var alpha = Minecraft.getInstance().level.getBlockTint(pos, world.);
        consumer.vertex(x, y, z)
                .color(red,green,blue,alpha)
                .uv(u, v)
                .normal(0.0f, 1.0f, 0.0f)
                .endVertex();
    }
}
