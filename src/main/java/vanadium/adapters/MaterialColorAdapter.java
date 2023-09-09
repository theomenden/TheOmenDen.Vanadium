package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.world.level.material.MapColor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MaterialColorAdapter extends TypeAdapter<MapColor> {
    private static final Map<String, MapColor> MATERIAL_COLORS = new HashMap<>(
            Map.<String, MapColor>ofEntries(
                    Map.entry("air", MapColor.NONE),
                    Map.entry("grass", MapColor.GRASS),
                    Map.entry("sand", MapColor.SAND),
                    Map.entry("cloth", MapColor.WOOL),
                    Map.entry("tnt", MapColor.FIRE),
                    Map.entry("ice", MapColor.ICE),
                    Map.entry("iron", MapColor.METAL),
                    Map.entry("foliage", MapColor.PLANT),
                    Map.entry("snow", MapColor.SNOW),
                    Map.entry("white", MapColor.SNOW),
                    Map.entry("clay", MapColor.CLAY),
                    Map.entry("dirt", MapColor.DIRT),
                    Map.entry("stone", MapColor.STONE),
                    Map.entry("water", MapColor.WATER),
                    Map.entry("wood", MapColor.WOOD),
                    Map.entry("quartz", MapColor.QUARTZ),
                    Map.entry("adobe", MapColor.COLOR_ORANGE),
                    Map.entry("orange", MapColor.COLOR_ORANGE),
                    Map.entry("magenta", MapColor.COLOR_MAGENTA),
                    Map.entry("light_blue", MapColor.COLOR_LIGHT_BLUE),
                    Map.entry("yellow", MapColor.COLOR_YELLOW),
                    Map.entry("lime", MapColor.COLOR_LIGHT_GREEN),
                    Map.entry("pink", MapColor.COLOR_PINK),
                    Map.entry("gray", MapColor.COLOR_GRAY),
                    Map.entry("light_gray", MapColor.COLOR_LIGHT_GRAY),
                    Map.entry("cyan", MapColor.COLOR_CYAN),
                    Map.entry("purple", MapColor.COLOR_PURPLE),
                    Map.entry("blue", MapColor.COLOR_BLUE),
                    Map.entry("brown", MapColor.COLOR_BROWN),
                    Map.entry("green", MapColor.COLOR_GREEN),
                    Map.entry("red", MapColor.COLOR_RED),
                    Map.entry("black", MapColor.COLOR_BLACK),
                    Map.entry("gold", MapColor.GOLD),
                    Map.entry("diamond", MapColor.DIAMOND),
                    Map.entry("lapis", MapColor.LAPIS),
                    Map.entry("emerald", MapColor.EMERALD),
                    Map.entry("podzol", MapColor.PODZOL),
                    Map.entry("netherrack", MapColor.NETHER),
                    Map.entry("white_terracotta", MapColor.TERRACOTTA_WHITE),
                    Map.entry("orange_terracotta", MapColor.TERRACOTTA_ORANGE),
                    Map.entry("magenta_terracotta", MapColor.TERRACOTTA_MAGENTA),
                    Map.entry("light_blue_terracotta", MapColor.TERRACOTTA_LIGHT_BLUE),
                    Map.entry("yellow_terracotta", MapColor.TERRACOTTA_YELLOW),
                    Map.entry("lime_terracotta", MapColor.TERRACOTTA_LIGHT_GREEN),
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
            ));

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