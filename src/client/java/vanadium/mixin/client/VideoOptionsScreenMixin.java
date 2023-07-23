package vanadium.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vanadium.configuration.VanadiumBlendingConfiguration;

@Mixin(value = VideoOptionsScreen.class)
public abstract class VideoOptionsScreenMixin extends GameOptionsScreen {
    @Unique
    @Final
    protected GameOptions gameOptions;

    public VideoOptionsScreenMixin(Screen screen, GameOptions options, Text title) {
        super(screen,options,title);
    }

    @ModifyArg(method = "init",
    at = @At(value = "INVOKE",
    target = "Lnet/minecraft/client/gui/screen/option/VideoOptionsScreen;getOptions(Lnet/minecraft/client/option/GameOptions;)[Lnet/minecraft/client/option/SimpleOption;"))
    protected GameOptions onInit(GameOptions gameOptions) {

        if(gameOptions.getBiomeBlendRadius() == this.gameOptions.getBiomeBlendRadius()) {
            gameOptions.getBiomeBlendRadius().setValue(VanadiumBlendingConfiguration.getBlendingRadius());
        }

        return gameOptions;
    }
}
