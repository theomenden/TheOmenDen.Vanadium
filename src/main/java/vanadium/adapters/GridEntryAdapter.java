package vanadium.adapters;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;
import vanadium.models.GridEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GridEntryAdapter extends TypeAdapter<GridEntry> {
    private final ResourceLocationAdapter resourceLocationAdapter = new ResourceLocationAdapter();
    @Override
    public void write(JsonWriter jsonWriter, GridEntry gridEntry) {
        throw new UnsupportedOperationException("Writing not supported at this time");
    }

    @Override
    public GridEntry read(JsonReader jsonReader) throws IOException {
        switch(jsonReader.peek()) {
            case NULL -> {
                jsonReader.nextNull();
                throw new JsonSyntaxException("Null value not allowed");
            }
            case STRING -> {
                var biomeIdentifier = this.resourceLocationAdapter.read(jsonReader);
                var gridEntry= new GridEntry();
                gridEntry.biomes = ImmutableList.of(biomeIdentifier);
                return gridEntry;
            }
            default -> {
                GridEntry gridEntry = new GridEntry();
                return resolveJsonData(jsonReader, gridEntry);
            }
        }
    }

    private GridEntry resolveJsonData(JsonReader in, GridEntry gridEntry) throws IOException {
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "biomes" -> gridEntry.biomes = readBiomes(in);
                case "column" -> gridEntry.column = in.nextInt();
                case "width" -> gridEntry.width = in.nextInt();
                default -> in.skipValue();
            }
        }
        in.endObject();
        return gridEntry;
    }

    private List<ResourceLocation> readBiomes(JsonReader in) throws IOException {
        List<ResourceLocation> biomes = new ArrayList<>();
        in.beginArray();
        while (in.hasNext()) {
            biomes.add(this.resourceLocationAdapter.read(in));
        }
        in.endArray();
        return biomes;
    }
}