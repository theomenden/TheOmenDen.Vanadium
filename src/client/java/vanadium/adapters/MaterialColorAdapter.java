package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.world.level.material.MapColor;

import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;

public class MaterialColorAdapter extends TypeAdapter<MapColor> {
    private static final Map<String, MapColor> MATERIAL_COLORS = Map.ofEntries(
        entry("air", MapColor.NONE),
        entry("grass", MapColor.GRASS),
        entry("sand", MapColor.SAND),
        entry("cloth", MapColor.COLOR_GRAY),
        entry("tnt", MapColor.COLOR_RED),
        entry("ice", MapColor.ICE),
        entry("iron", MapColor.RAW_IRON),
        entry("foliage", MapColor.PLANT),
        entry("snow", MapColor.SNOW),
        entry("white", MapColor.SNOW),
        entry("clay", MapColor.CLAY),
        entry("dirt", MapColor.DIRT),
        entry("stone", MapColor.STONE),
        entry("water", MapColor.WATER),
        entry("wood", MapColor.WOOD),
        entry("quartz", MapColor.QUARTZ),
        entry("adobe", MapColor.COLOR_ORANGE),
        entry("orange", MapColor.COLOR_ORANGE),
        entry("magenta", MapColor.COLOR_MAGENTA),
        entry("light_blue", MapColor.COLOR_LIGHT_BLUE),
        entry("yellow", MapColor.COLOR_YELLOW),
        entry("lime", MapColor.COLOR_LIGHT_GREEN),
        entry("pink", MapColor.COLOR_PINK),
        entry("gray", MapColor.COLOR_GRAY),
        entry("light_gray", MapColor.COLOR_LIGHT_GRAY),
        entry("cyan", MapColor.COLOR_CYAN),
        entry("purple", MapColor.COLOR_PURPLE),
        entry("blue", MapColor.COLOR_BLUE),
        entry("brown", MapColor.COLOR_BROWN),
        entry("green", MapColor.COLOR_GREEN),
        entry("red", MapColor.COLOR_RED),
        entry("black", MapColor.COLOR_BLACK),
        entry("gold", MapColor.GOLD),
        entry("diamond", MapColor.DIAMOND),
        entry("lapis", MapColor.LAPIS),
        entry("emerald", MapColor.EMERALD),
        entry("podzol", MapColor.PODZOL),
        entry("netherrack", MapColor.NETHER),
        entry("white_terracotta", MapColor.TERRACOTTA_WHITE),
        entry("orange_terracotta", MapColor.TERRACOTTA_ORANGE),
        entry("magenta_terracotta", MapColor.TERRACOTTA_MAGENTA),
        entry("light_blue_terracotta", MapColor.TERRACOTTA_LIGHT_BLUE),
        entry("yellow_terracotta", MapColor.TERRACOTTA_YELLOW),
        entry("lime_terracotta", MapColor.TERRACOTTA_LIGHT_GREEN),
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
