package vanadium.mixin.coloring.model;

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
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.customcolors.decorators.ModelIdContext;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.mixin.coloring.BlockColorsAccessor;


@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Unique
    private static final BlockStateArgumentType BLOCK_STATE_PARSER = BlockStateArgumentType.blockState(
            CommandRegistryAccess.of(
                    DynamicRegistryManager.of(Registries.REGISTRIES).toImmutable(),
                    FeatureFlags.DEFAULT_ENABLED_FEATURES
            )
    );

    @Inject(
            method = "loadModel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;putModel(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/render/model/UnbakedModel;)V")
    )
    private void onModelLoad(Identifier id, CallbackInfo ci) {
        ModelIdContext.shouldTintCurrentModel = false;

        if(id instanceof ModelIdentifier modelId){
            BlockState blockState;
            if(modelId.getVariant().equals("inventory")) {
                var blockId = Identifier.of(modelId.getNamespace(), modelId.getPath());
                blockState = Registries.BLOCK.get(blockId).getDefaultState();
            } else {
                var blockStateDescriptor = modelId.getNamespace() + ":" + modelId.getPath() + "[" + modelId.getVariant() + "]";

                try {
                    blockState = BLOCK_STATE_PARSER.parse(new StringReader(blockStateDescriptor)).getBlockState();
                } catch(CommandSyntaxException e) {
                    return;
                }
            }

            if(BiomeColorMappings.isCustomColored(blockState)) {
                var colorProviders = ((BlockColorsAccessor) MinecraftClient.getInstance().getBlockColors()).getProviders();

                if(!colorProviders.containsKey(Registries.BLOCK.getRawId(blockState.getBlock()))) {
                    ModelIdContext.shouldTintCurrentModel = true;
                }
            }
        }

    }

}
