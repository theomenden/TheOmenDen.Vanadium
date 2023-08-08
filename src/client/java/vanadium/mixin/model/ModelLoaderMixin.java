package vanadium.mixin.model;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.colormapping.BiomeColorMappings;
import vanadium.mixin.block.BlockColorsAccessor;
import vanadium.models.ModelIdContext;

@Mixin(ModelManager.class)
public abstract class ModelLoaderMixin {

    private static final BlockStateArgument BLOCK_STATE_PARSER = BlockStateArgument.block(CommandBuildContext.configurable(RegistryAccess.EMPTY, FeatureFlagSet.of()));

    @Dynamic("Model baking Lambda in ")
    @Inject(method = "loadModels",
    at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/ModelBakery;bakeModels(Ljava/util/function/BiFunction;)V"
    ))
    private void setModelResourceLocationContext(ResourceLocation resourceLocation, CallbackInfo ci) {
        ModelIdContext.isACustomTintForCurrentModel = false;
        if(resourceLocation instanceof ModelResourceLocation modelId) {
            BlockState blockState;
            if(modelId.getVariant().equals("inventory")) {
                ResourceLocation blockid = ResourceLocation.tryBuild(modelId.getNamespace(), modelId.getPath());
                blockState = BuiltInRegistries.BLOCK.get(blockid).defaultBlockState();
            } else {
                var blockStateDescription = modelId.getNamespace() + ":" + modelId.getPath() + "[" + modelId.getVariant() + "]";

                try {
                    blockState = BLOCK_STATE_PARSER.parse(new StringReader(blockStateDescription)).getState();
                } catch(CommandSyntaxException e) {
                    return;
                }
            }

            if(BiomeColorMappings.isCustomColored(blockState)) {
                var colorProviders = ((BlockColorsAccessor) Minecraft.getInstance().getBlockColors()).getBlockColors();

                if(!colorProviders.contains(BuiltInRegistries.BLOCK.getId(blockState.getBlock()))) {
                    ModelIdContext.isACustomTintForCurrentModel = true;
                }
            }
        }
    }

}
