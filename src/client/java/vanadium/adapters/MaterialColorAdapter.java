package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.block.MapColor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import static java.util.Map.entry;

public class MaterialColorAdapter extends TypeAdapter<MapColor> {
    private static final Map<String, MapColor> MATERIAL_COLORS = Map.ofEntries(
        entry("air", MapColor.CLEAR),
        entry("grass", MapColor.PALE_GREEN),
        entry("sand", MapColor.PALE_YELLOW),
        entry("cloth", MapColor.WHITE_GRAY),
        entry("tnt", MapColor.BRIGHT_RED),
        entry("ice", MapColor.PALE_PURPLE),
        entry("iron", MapColor.IRON_GRAY),
        entry("foliage", MapColor.DARK_GREEN),
        entry("snow", MapColor.WHITE),
        entry("white", MapColor.WHITE),
        entry("clay", MapColor.LIGHT_BLUE_GRAY),
        entry("dirt", MapColor.DIRT_BROWN),
        entry("stone", MapColor.STONE_GRAY),
        entry("water", MapColor.WATER_BLUE),
        entry("wood", MapColor.OAK_TAN),
        entry("quartz", MapColor.OFF_WHITE),
        entry("adobe", MapColor.ORANGE),
        entry("orange", MapColor.ORANGE),
        entry("magenta", MapColor.MAGENTA),
        entry("light_blue", MapColor.LIGHT_BLUE),
        entry("yellow", MapColor.YELLOW),
        entry("lime", MapColor.LIME),
        entry("pink", MapColor.PINK),
        entry("gray", MapColor.GRAY),
        entry("light_gray", MapColor.LIGHT_GRAY),
        entry("cyan", MapColor.CYAN),
        entry("purple", MapColor.PURPLE),
        entry("blue", MapColor.BLUE),
        entry("brown", MapColor.BROWN),
        entry("green", MapColor.GREEN),
        entry("red", MapColor.RED),
        entry("black", MapColor.BLACK),
        entry("gold", MapColor.GOLD),
        entry("diamond", MapColor.DIAMOND_BLUE),
        entry("lapis", MapColor.LAPIS_BLUE),
        entry("emerald", MapColor.EMERALD_GREEN),
        entry("podzol", MapColor.SPRUCE_BROWN),
        entry("netherrack", MapColor.DARK_RED),
        entry("white_terracotta", MapColor.TERRACOTTA_WHITE),
        entry("orange_terracotta", MapColor.TERRACOTTA_ORANGE),
        entry("magenta_terracotta", MapColor.TERRACOTTA_MAGENTA),
        entry("light_blue_terracotta", MapColor.TERRACOTTA_LIGHT_BLUE),
        entry("yellow_terracotta", MapColor.TERRACOTTA_YELLOW),
        entry("lime_terracotta", MapColor.TERRACOTTA_LIME),
        entry("pink_terracotta", MapColor.TERRACOTTA_PINK),
        entry("gray_terracotta", MapColor.TERRACOTTA_GRAY),
        entry("light_gray_terracotta", MapColor.TERRACOTTA_LIGHT_GRAY),
        entry("cyan_terracotta", MapColor.TERRACOTTA_CYAN),
        entry("purple_terracotta", MapColor.TERRACOTTA_PURPLE),
        entry("blue_terracotta", MapColor.TERRACOTTA_BLUE),
        entry("brown_terracotta", MapColor.TERRACOTTA_BROWN),
        entry("green_terracotta", MapColor.TERRACOTTA_GREEN),
        entry("red_terracotta", MapColor.TERRACOTTA_RED),
        entry("black_terracotta", MapColor.TERRACOTTA_BLACK)
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
