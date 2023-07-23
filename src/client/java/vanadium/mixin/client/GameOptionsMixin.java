package vanadium.mixin.client;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.configuration.VanadiumBlendingConfiguration;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin {
    @Shadow public abstract SimpleOption<Integer> getBiomeBlendRadius();

    @Inject(method="load", at=@At("HEAD"))
    private void onInit(CallbackInfo ci) {
        getBiomeBlendRadius().setValue(VanadiumBlendingConfiguration.getBlendingRadius());
    }

}
