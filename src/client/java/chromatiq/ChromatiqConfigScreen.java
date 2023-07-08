package chromatiq;

import java.util.List;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

public class ChromatiqConfigScreen extends Screen {
    private final Screen parent;
    private final ChromatiqConfig config;

    public ChromatiqConfigScreen(Screen parent, ChromatiqConfig config) {
        super(Text.translatable(getTranslationKey("title")));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        addDrawableChild(null);
        addDrawableChild(null);
        addDrawableChild(null);
        addDrawableChild(null);
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, title, width * 0.5, 10, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private static String getTranslationKey(String key) {
        return "options.chromatiq." + key;
    }

    private static String getTooltipKey(String key) {
        return key+".tooltip";
    }

    private ButtonWidget.ToolTipSupplier createDefaultTooltipSupplier(StringVisitable text) {
        return (button, matrices, mouseX, mouseY) -> {
            renderOrderedTooltip(matrices, textRenderer.wrapLines(text, width * 0.5));
        };
    }

    private ButtonWidget createBooleanOptionButton(int x, int y, int width, int height, Option<Boolean> option) {
        String translationKey = getTranslationKey(option.getKey());
        Text text = Text.translatable(translationKey);
        Text tooltipText = Text.translatable(getTooltipKey(translationKey));
        return new ButtonWidget(x, y, width, height, ScreenTexts.composeToggleText(text, option.get()),
                button -> {
                    boolean newValue = !option.get();
                    button.setMessage(ScreenTexts.composeToggleText(text, newValue));
                    option.set(newValue);
                },
                this.createDefaultTooltipSupplier(tooltipText)
        );
    }
}
