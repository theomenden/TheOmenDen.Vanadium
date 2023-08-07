package vanadium.mixin.model;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.models.ModelIdContext;

import java.util.List;

@Mixin(BlockModel.class)
public abstract class BlockModelMixin {

    @Shadow @Final private List<BlockElement> elements;

    @Inject(
            method = "bake",
            at = @At("HEAD")
    )
    private void setVanadiumCustomTint(CallbackInfoReturnable<BakedModel> cir) {
        if (ModelIdContext.isACustomTintForCurrentModel
                && this.elements
                .stream()
                .flatMap(element -> element.faces
                        .values()
                        .stream())
                .anyMatch(face -> face.tintIndex >= 0)) {
            ModelIdContext.isACustomTintForCurrentModel = false;
        }
    }
}
