package vanadium.mixin.coloring.model;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.customcolors.decorators.ModelIdContext;
import vanadium.customcolors.mapping.BiomeColorMappings;
import vanadium.mixin.coloring.BlockColorsAccessor;

import java.util.function.Predicate;


@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow @Final private static String BUILTIN;

    @Shadow
    private static Predicate<BlockState> stateKeyToPredicate(StateManager<Block, BlockState> stateFactory, String key) {
        return null;
    }

    @Unique
    private static final BlockStateArgumentType BLOCK_STATE_PARSER = BlockStateArgumentType.blockState(
            CommandRegistryAccess.of(DynamicRegistryManager.EMPTY, FeatureSet.empty()));

    @Inject(
            method = "method_4736",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/DefaultedRegistry;get(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;")
    )
    private static void onModelLoad(Identifier identifier, CallbackInfoReturnable<StateManager> cir) {
        ModelIdContext.shouldTintCurrentModel = false;

        if(identifier instanceof ModelIdentifier modelId){
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
