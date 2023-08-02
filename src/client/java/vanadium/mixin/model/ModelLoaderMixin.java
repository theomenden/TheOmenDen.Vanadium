package vanadium.mixin.model;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(BakedModel.class)
public class ModelLoaderMixin {
    private static final BlockStateArgument BLOCK_STATE_ARGUMENT = BlockStateArgument.block(
           CommandBuildContext.configurable(RegistryAccess.EMPTY, FeatureFlagSet.of())
    );

    @Dynamic("Lambda in upload")
    @Inject(
            method = ""
    )
}
