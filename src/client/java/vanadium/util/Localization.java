package vanadium.util;

import net.minecraft.network.chat.contents.TranslatableContents;
import vanadium.Vanadium;

public final class Localization {
    private Localization() {}
    public static String createTranslationkey(String domain, String path) {
        return domain + "." + Vanadium.MODID + path;
    }

    public static TranslatableContents createLocalizedText(String domain, String path, Object... args) {
        return new TranslatableContents(createTranslationkey(domain, path), "", args);
    }
}
