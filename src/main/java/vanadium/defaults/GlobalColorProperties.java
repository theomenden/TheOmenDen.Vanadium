package vanadium.defaults;

import com.google.gson.JsonParseException;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import vanadium.Vanadium;
import vanadium.models.enums.ColoredParticle;
import vanadium.models.enums.ColumnLayout;
import vanadium.models.enums.Format;
import vanadium.models.records.VanadiumColor;
import vanadium.utils.ColorConverter;
import vanadium.utils.GsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class GlobalColorProperties {
    private static final Logger LOGGER = LogManager.getLogger(Vanadium.MODID);

    private static final Map<String, String> keyRemap = Map.ofEntries(
            Map.entry("nether", "the_nether"),
            Map.entry("end", "the_end"),
            Map.entry("lightBlue", "light_blue"),
            Map.entry("silver", "light_gray"),
            Map.entry("moveSpeed", "speed"),
            Map.entry("moveSlowdown", "slowness"),
            Map.entry("digSpeed", "haste"),
            Map.entry("digSlowDown", "mining_fatigue"),
            Map.entry("damageBoost", "strength"),
            Map.entry("heal", "instant_health"),
            Map.entry("harm", "instant_damage"),
            Map.entry("jump", "jump_boost"),
            Map.entry("confusion", "nausea"),
            Map.entry("fireResistance", "fire_resistance"),
            Map.entry("waterBreathing", "water_breathing"),
            Map.entry("nightVision", "night_vision"),
            Map.entry("healthBoost", "health_boost")
    );

    public static GlobalColorProperties DEFAULT = new GlobalColorProperties(new Settings());

    private final Map<ColoredParticle, VanadiumColor> particle;
    private final Map<ResourceLocation, VanadiumColor> dimensionFog;
    private final Map<ResourceLocation, VanadiumColor> dimensionSky;
    private final int lilypad;
    private final Map<MobEffect, VanadiumColor> potions;
    private final Map<DyeColor, VanadiumColor> sheep;
    private final Map<DyeColor, float[]> sheepRgb;
    private final Map<DyeColor, VanadiumColor> collar;
    private final Map<DyeColor, float[]> collarRgb;
    private final Map<DyeColor, VanadiumColor> banner;
    private final Map<DyeColor, float[]> bannerRgb;
    private final Map<MapColor, VanadiumColor> map;
    private final Map<EntityType<?>, int[]> spawnEgg;
    private final Map<ChatFormatting, TextColor> textColor;
    private final TextColorSettings text;


    @Getter
    private final int xpOrbTime;
    @Getter
    private final Format defaultFormat;
    private final @Nullable ColumnLayout defaultLayout;

    private GlobalColorProperties(Settings settings) {
        this.particle = settings.particle;
        this.dimensionFog = convertIdentifierMapping(settings.fog);
        this.dimensionSky = convertIdentifierMapping(settings.sky);
        this.lilypad = settings.lilypad != null ? settings.lilypad.rgb() : 0;
        this.potions = convertMapping(settings.potion, BuiltInRegistries.MOB_EFFECT);
        this.sheep = settings.sheep;
        this.sheepRgb = toRgb(settings.sheep);
        this.collar = settings.collar;
        this.collarRgb = toRgb(settings.collar);
        this.banner = settings.banner;
        this.bannerRgb = toRgb(settings.banner);
        this.map = settings.map;
        this.spawnEgg = collateSpawnEggColors(settings);
        this.xpOrbTime = settings.xporb.time;
        if(settings.text != null) {
            TextColorSettings text = settings.text;
            this.textColor = new HashMap<>();
            text.code
                    .forEach((key, value) -> {
                        int code = key;
                        if (code < 16) {
                            ChatFormatting color = ChatFormatting.getById(code);
                            textColor.put(color, TextColor.fromRgb(value
                                    .rgb()));
                        }
                    });
            text.format
                    .forEach((key, value) -> this.textColor.put(key, TextColor.fromRgb(value
                            .rgb())));
            text.code = Collections.emptyMap();
            text.format = Collections.emptyMap();
            this.text = text;
        } else {
            this.textColor = Collections.emptyMap();
            this.text = new TextColorSettings();
        }
        this.defaultFormat = settings.palette.format;
        this.defaultLayout = settings.palette.layout;
        VanadiumColor  water = settings.potion.get("water");
        if(water == null) {
            water = settings.potion.get("minecraft:water");
        }
        if(water != null) {
            this.potions.put(null, water);
        }
    }

    public static GlobalColorProperties load(ResourceManager manager, ResourceLocation id, boolean isFalling) {
        try(InputStream inputStream = manager.getResourceOrThrow(id).open();
            Reader reader = GsonUtils.getJsonReader(inputStream, id, k -> keyRemap.getOrDefault(k,k), k -> false)) {
            return loadFromJson(reader, id);
        } catch (IOException e) {
            return isFalling? DEFAULT : null;
        }
    }

    public int getParticle(ColoredParticle particleKey) {
        return getColor(particleKey, particle);
    }

    public int getDimensionFog(ResourceLocation dimensionKey) {
        return getColor(dimensionKey, dimensionFog);
    }

    public int getDimensionSky(ResourceLocation dimensionKey) {
        return getColor(dimensionKey, dimensionSky);
    }

    public int getLilyPad() {
        return lilypad;
    }

    public int getPotion(MobEffect mobEffect) {
        return getColor(mobEffect, potions);
    }

    public int getWool(DyeColor color) {
        return getColor(color, sheep);
    }

    public float[] getWoolRgb(DyeColor color) {
        return sheepRgb.get(color);
    }

    public int getCollar(DyeColor color) {
        return getColor(color, collar);
    }

    public float[] getCollarRgb(DyeColor color) {
        return collarRgb.get(color);
    }

    public int getBanner(DyeColor color) {
        return getColor(color, banner);
    }

    public float[] getBannerRgb(DyeColor color) {
        return bannerRgb.get(color);
    }

    public int getMap(MapColor color) {
        return getColor(color, map);
    }

    public int getSpawnEgg(EntityType<?> type, int index) {
        int[] colors = spawnEgg.get(type);
        return colors != null? colors[index] : 0;
    }

    public TextColor getText(ChatFormatting formatting) {
        return textColor.get(formatting);
    }

    public int getHoveredButtonText() {
        return getColor(text.button.hover);
    }

    public int getDisabledButtonText() {
        return getColor(text.button.disabled);
    }


    public int getXpText() {
        return getColor(text.xpbar);
    }

    public int getSignText(DyeColor color) {
        return getColor(color, text.sign);
    }

    public @Nullable ColumnLayout getDefaultColumnLayout() {
        return defaultLayout;
    }

    private int getColor(VanadiumColor color) {
        return color != null ? color.rgb() : 0;
    }

    private Map<ResourceLocation, VanadiumColor> convertIdentifierMapping(Map<String, VanadiumColor> map) {
        Map<ResourceLocation, VanadiumColor> result = new HashMap<>();
        map
                .forEach((key, value) -> {
                    ResourceLocation id = ResourceLocation.tryParse(key);
                    if (id != null) {
                        result.put(id, value);
                    }
                });
        return result;
    }

    private static <T> Map<T, VanadiumColor> convertMapping(Map<String, VanadiumColor> initialColor, Registry<T> registry) {
        Map<T, VanadiumColor> result = new HashMap<>();
        initialColor
                .forEach((key1, value) -> {
                    T key = registry.get(ResourceLocation.tryParse(key1));
                    if (key != null) {
                        result.put(key, value);
                    }
                });
        return result;
    }

    private static <T> Map<T, float[]> toRgb(Map<T, VanadiumColor> map) {
        Map<T, float[]> result = new HashMap<>();
        map
                .forEach((key, value) -> {
                    float[] rgb = ColorConverter.createColorFloatArray(value.rgb());
                    result.put(key, rgb);
                });
        return result;
    }

    private static Map<EntityType<?>, int[]> collateSpawnEggColors(Settings settings) {
        Map<EntityType<?>, int[]> result = new HashMap<>();

        var entityTypeRegistry =  BuiltInRegistries.ENTITY_TYPE;

        if(settings.egg != null) {
            LegacyEggColor legacy = settings.egg;
            legacy.shell
                    .forEach((key, value) -> {
                        EntityType<?> type = entityTypeRegistry.get(ResourceLocation.tryParse(key));
                        result.put(type, new int[]{value.rgb(), 0});
                    });
            legacy.spots
                    .forEach((key, value) -> {
                        EntityType<?> type = entityTypeRegistry.get(ResourceLocation.tryParse(key));
                        int[] colors = result.computeIfAbsent(type, t -> new int[2]);
                        colors[1] = value
                                .rgb();
                    });
        }

        settings.spawnEgg.forEach((key, vanadiumColors) -> {
            EntityType<?> type = entityTypeRegistry.get(ResourceLocation.tryParse(key));
            int[] colors = result.computeIfAbsent(type, t -> new int[2]);

            IntStream
                    .range(0, Math.min(2, vanadiumColors.length))
                    .forEach(i -> colors[i] = vanadiumColors[i]
                            .rgb());
        });

        return result;
    }

    private static <T> int getColor(T key, Map<T, VanadiumColor> map) {
        VanadiumColor color = map.get(key);
        return color != null ? color.rgb() : 0;
    }

    private static GlobalColorProperties loadFromJson(Reader rd, ResourceLocation id) {
        Settings settings;
        try {
            settings = GsonUtils.PROPERTY_GSON.fromJson(rd, Settings.class);

            if(settings == null) {
                settings = new Settings();
            }
        } catch (JsonParseException e) {
            LOGGER.error("Error parsing {} : {}", id, e.getMessage());
            settings = new Settings();
        }
        return new GlobalColorProperties(settings);
    }

    private static class Settings {
        Map<ColoredParticle, VanadiumColor> particle = Collections.emptyMap();
        Map<String, VanadiumColor> fog = Collections.emptyMap();
        Map<String, VanadiumColor> sky = Collections.emptyMap();
        VanadiumColor lilypad;
        Map<String, VanadiumColor> potion = Collections.emptyMap();
        Map<DyeColor, VanadiumColor> sheep = Collections.emptyMap();
        Map<DyeColor, VanadiumColor> collar = Collections.emptyMap();
        Map<DyeColor, VanadiumColor> banner = Collections.emptyMap();
        Map<MapColor, VanadiumColor> map = Collections.emptyMap();
        Map<String, VanadiumColor[]> spawnEgg = Collections.emptyMap();
        LegacyEggColor egg;
        TextColorSettings text;
        XpOrb xporb = XpOrb.DEFAULT;
        GlobalColorProperties.Palette palette = GlobalColorProperties.Palette.DEFAULT;
    }

    private static class LegacyEggColor {
        Map<String, VanadiumColor> shell = Collections.emptyMap();
        Map<String, VanadiumColor> spots = Collections.emptyMap();
    }

    private static class TextColorSettings {
        VanadiumColor xpbar;
        ButtonText button = new ButtonText();
        Map<DyeColor, VanadiumColor> sign = Collections.emptyMap();
        Map<ChatFormatting, VanadiumColor> format = Collections.emptyMap();
        Map<Integer, VanadiumColor> code = Collections.emptyMap();

        static class ButtonText {
            VanadiumColor hover;
            VanadiumColor disabled;
        }

    }

    private static class XpOrb {
        static XpOrb DEFAULT = new XpOrb();
        int time = 628;
    }

    private static class Palette {
        static Palette DEFAULT = new Palette();

        Format format = Format.VANILLA;
        @Nullable
        ColumnLayout layout = null;
    }
}
