package vanadium.mixin.model;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.colormapping.BiomeColorMappings;
import vanadium.mixin.block.BlockColorsAccessor;
import vanadium.models.ModelIdContext;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Unique
    private static final BlockStateArgumentType BLOCK_STATE_PARSER = BlockStateArgumentType.blockState(CommandRegistryAccess.of(DynamicRegistryManager.ImmutableImpl.EMPTY, FeatureSet.empty()));

    @Dynamic("Model baking lambda in upload()")
    @Inject(
            method = "method_4733",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/model/ModelLoader;bake(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/ModelBakeSettings;)Lnet/minecraft/client/render/model/BakedModel;"
            )
    )
    private void setModelIdContext(Identifier id, CallbackInfo info) {
        ModelIdContext.isACustomeTintForCurrentModel = false;
        if(id instanceof ModelIdentifier modelId) {
            BlockState blockState;
            if(modelId.getVariant().equals("inventory")) {
                // we're using the block color providers for detecting non-custom item tinting for now
                var blockId = new Identifier(modelId.getNamespace(), modelId.getPath());
                blockState = Registries.BLOCK.get(blockId).getDefaultState();
            } else {
                var blockStateDesc = modelId.getNamespace() + ":" + modelId.getPath() + "[" + modelId.getVariant() + "]";
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
                var colorProviders = ((BlockColorsAccessor) MinecraftClient
                        .getInstance().getBlockColors()).getProviders();
                if(!colorProviders.containsKey(Registries.BLOCK.getRawId(blockState.getBlock()))) {
                    // tentatively set to true - further checking in JsonUnbakedModelMixin
                    ModelIdContext.isACustomeTintForCurrentModel = true;
                }
            }
        }
    }
}
