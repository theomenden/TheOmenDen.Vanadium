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
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.customcolors.decorators.ModelIdContext;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.mixin.coloring.BlockColorsAccessor;

import java.util.function.BiFunction;


@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Unique
    private static final BlockStateArgumentType BLOCK_STATE_PARSER = BlockStateArgumentType.blockState(
            CommandRegistryAccess.of(
                    DynamicRegistryManager.of(Registries.REGISTRIES).toImmutable(),
                    FeatureFlags.DEFAULT_ENABLED_FEATURES
            )
    );

    @Dynamic("Lambda in bake")
    @Inject(
            method = "method_45877",
            at= @At(value ="INVOKE", ordinal = 0, shift = At.Shift.AFTER)
    )
    private void setModelIdContext(BiFunction biFunction, Identifier id, CallbackInfo ci) {
        boolean finished = false;
        ModelIdContext.shouldTintCurrentModel = false;
        if (id instanceof ModelIdentifier modelId) {
            BlockState blockState = null;
            if (modelId
                    .getVariant()
                    .equals("inventory")) {
                var blockId = new Identifier(modelId.getNamespace(), modelId.getPath());
                blockState = Registries.BLOCK
                        .get(blockId)
                        .getDefaultState();
            } else {
                var blockStateDesc = modelId.getNamespace() + ":" + modelId.getPath() + "[" + modelId.getVariant() + "]";
                try {
                    blockState = BLOCK_STATE_PARSER
                            .parse(new StringReader(blockStateDesc))
                            .getBlockState();
                } catch (CommandSyntaxException e) {
                    finished = true;
                }
            }
            if (!finished) {
                if (BiomeColorMappings.isCustomColored(blockState)) {
                    var colorProviders = ((BlockColorsAccessor) MinecraftClient
                            .getInstance()
                            .getBlockColors()).getProviders();
                    if (!colorProviders.containsKey(Registries.BLOCK.getRawId(blockState.getBlock()))) {
                        ModelIdContext.shouldTintCurrentModel = true;
                    }
                }
            }
        }
    }
}
