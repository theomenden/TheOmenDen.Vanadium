package vanadium.mixin.model;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.colormapping.BiomeColorMappings;
import vanadium.mixin.block.BlockColorsAccessor;
import vanadium.models.ModelIdContext;

import java.util.Map;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @Inject(
            method = "loadModels",
            at = @At(
                    value="INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ModelBakery;bakeModels(Ljava/util/function/BiFunction;)V"
            )
    )
    private void setModelResourceContext(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir) {
        ModelIdContext.isACustomeTintForCurrentModel = false;
        if(id instanceof ModelTemplate modelId) {
            BlockState blockState;
            if(modelId.getVariant().equals("inventory")) {
                // we're using the block color providers for detecting non-custom item tinting for now
                var blockId = new ResourceLocation(modelId.getNamespace(), modelId.getPath());
                blockState = BuiltInRegistries.BLOCK.get(blockId).getDefaultState();
            } else {
                var blockStateDesc = modelId.namespace() + ":" + modelId.getPath() + "[" + modelId.getVariant() + "]";
                try {
                    blockState = BLOCK_STATE_PARSER.parse(new StringReader(blockStateDesc)).getBlockState();
                } catch(CommandSyntaxException e) {
                    // don't custom tint block state models that aren't real blocks
                    return;
                }
            }
            // test first two criteria
            //  - Colormatic has custom colors for the block state
            //  - the block does not already have a color provider
            // note: we're calling a custom biome colors method. Re-evaluate if we combine custom biome colors
            // with provided biome colors.
            if(BiomeColorMappings.isCustomColored(blockState)) {
                var colorProviders = ((BlockColorsAccessor) Minecraft
                        .getInstance().getBlockColors()).getBlockColors();
                if(!colorProviders.containsKey(Registry.BLOCK.getRawId(blockState.getBlock()))) {
                    // tentatively set to true - further checking in JsonUnbakedModelMixin
                    ModelIdContext.isACustomeTintForCurrentModel = true;
                }
            }
        }
    }
}
