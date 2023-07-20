package vanadium.util;

import net.minecraft.text.Text;
import vanadium.Vanadium;

public final class Localization {
    private Localization() {}
    public static String createTranslationkey(String domain, String path) {
        return domain + "." + Vanadium.MODID + path;
    }

    public static Text createLocalizedText(String domain, String path, Object... args) {
        return Text.translatable(createTranslationkey(domain, path), args);
    }
}
