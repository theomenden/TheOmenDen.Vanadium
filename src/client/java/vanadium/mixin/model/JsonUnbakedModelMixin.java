package vanadium.mixin.model;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.models.ModelIdContext;

import java.util.List;

@Mixin(JsonUnbakedModel.class)
public abstract class JsonUnbakedModelMixin {
    @Shadow public abstract List<ModelElement> getElements();

    @Inject(
            method = "bake(Lnet/minecraft/client/render/model/Baker;Ljava/util/function/Function;Lnet/minecraft/client/render/model/ModelBakeSettings;Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/BakedModel;",
            at = @At("HEAD")
    )
    private void setVanadiumCustomTinted(CallbackInfoReturnable<BakedModel> info) {
        if(ModelIdContext.isACustomeTintForCurrentModel) {
            this
                    .getElements()
                    .stream()
                    .filter(element -> element.faces
                            .values()
                            .stream()
                            .anyMatch(face -> face.tintIndex >= 0))
                    .forEach(element -> ModelIdContext.isACustomeTintForCurrentModel = false);
        }
    }
}
