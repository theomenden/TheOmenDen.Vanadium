package vanadium.mixin.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.configuration.VanadiumBlendingConfiguration;

@Mixin(Options.class)
public abstract class GameOptionsMixin {
    @Shadow public abstract OptionInstance<Integer> biomeBlendRadius();

    @Inject(method="processOptions", at=@At("HEAD"))
    private void onInit(Options.FieldAccess fieldAccess, CallbackInfo ci) {
        fieldAccess.process("vanadiumBlendingRadius", VanadiumBlendingConfiguration.vanadiumBlendingRadius);
    }

}
