package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.block.MapColor;

import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;

public class MaterialColorAdapter extends TypeAdapter<MapColor> {
    private static final Map<String, MapColor> MATERIAL_COLORS = Map.<String, MapColor>ofEntries(
            Map.entry("air", MapColor.CLEAR),
            Map.entry("grass", MapColor.PALE_GREEN),
            Map.entry("sand", MapColor.PALE_YELLOW),
            Map.entry("cloth", MapColor.WHITE_GRAY),
            Map.entry("tnt", MapColor.BRIGHT_RED),
            Map.entry("ice", MapColor.PALE_PURPLE),
            Map.entry("iron", MapColor.IRON_GRAY),
            Map.entry("foliage", MapColor.DARK_GREEN),
            Map.entry("snow", MapColor.WHITE),
            Map.entry("white", MapColor.WHITE),
            Map.entry("clay", MapColor.LIGHT_BLUE_GRAY),
            Map.entry("dirt", MapColor.DIRT_BROWN),
            Map.entry("stone", MapColor.STONE_GRAY),
            Map.entry("water", MapColor.WATER_BLUE),
            Map.entry("wood", MapColor.OAK_TAN),
            Map.entry("quartz", MapColor.OFF_WHITE),
            Map.entry("adobe", MapColor.ORANGE),
            Map.entry("orange", MapColor.ORANGE),
            Map.entry("magenta", MapColor.MAGENTA),
            Map.entry("light_blue", MapColor.LIGHT_BLUE),
            Map.entry("yellow", MapColor.YELLOW),
            Map.entry("lime", MapColor.LIME),
            Map.entry("pink", MapColor.PINK),
            Map.entry("gray", MapColor.GRAY),
            Map.entry("light_gray", MapColor.LIGHT_GRAY),
            Map.entry("cyan", MapColor.CYAN),
            Map.entry("purple", MapColor.PURPLE),
            Map.entry("blue", MapColor.BLUE),
            Map.entry("brown", MapColor.BROWN),
            Map.entry("green", MapColor.GREEN),
            Map.entry("red", MapColor.RED),
            Map.entry("black", MapColor.BLACK),
            Map.entry("gold", MapColor.GOLD),
            Map.entry("diamond", MapColor.DIAMOND_BLUE),
            Map.entry("lapis", MapColor.LAPIS_BLUE),
            Map.entry("emerald", MapColor.EMERALD_GREEN),
            Map.entry("podzol", MapColor.SPRUCE_BROWN),
            Map.entry("netherrack", MapColor.DARK_RED),
            Map.entry("white_terracotta", MapColor.TERRACOTTA_WHITE),
            Map.entry("orange_terracotta", MapColor.TERRACOTTA_ORANGE),
            Map.entry("magenta_terracotta", MapColor.TERRACOTTA_MAGENTA),
            Map.entry("light_blue_terracotta", MapColor.TERRACOTTA_LIGHT_BLUE),
            Map.entry("yellow_terracotta", MapColor.TERRACOTTA_YELLOW),
            Map.entry("lime_terracotta", MapColor.TERRACOTTA_LIME),
            Map.entry("pink_terracotta", MapColor.TERRACOTTA_PINK),
            Map.entry("gray_terracotta", MapColor.TERRACOTTA_GRAY),
            Map.entry("light_gray_terracotta", MapColor.TERRACOTTA_LIGHT_GRAY),
            Map.entry("cyan_terracotta", MapColor.TERRACOTTA_CYAN),
            Map.entry("purple_terracotta", MapColor.TERRACOTTA_PURPLE),
            Map.entry("blue_terracotta", MapColor.TERRACOTTA_BLUE),
            Map.entry("brown_terracotta", MapColor.TERRACOTTA_BROWN),
            Map.entry("green_terracotta", MapColor.TERRACOTTA_GREEN),
            Map.entry("red_terracotta", MapColor.TERRACOTTA_RED),
            Map.entry("black_terracotta", MapColor.TERRACOTTA_BLACK)
    );

    @Override
    public void write(JsonWriter jsonWriter, MapColor mapColor) throws IOException {
        throw new UnsupportedOperationException("writing is not supported");
    }

    @Override
    public MapColor read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonSyntaxException("Required value cannot be null");
        }
        String readValue = jsonReader.nextString();
        return MATERIAL_COLORS.get(readValue);
    }
}