package vanadium.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.gui.screen.option.SimpleOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = VideoOptionsScreen.class)
public abstract class VideoOptionsScreenMixin extends SimpleOptionsScreen {
    @Shadow
    private SimpleOption option;


    public VideoOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title, SimpleOption<?>[] options) {
        super(parent, gameOptions, title, options);
    }
}
