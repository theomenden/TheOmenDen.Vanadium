package vanadium.adapters;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;
import vanadium.models.ItemsGrid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemsAdapter extends TypeAdapter<ItemsGrid> {
    private final IdentifierAdapter identifierAdapter = new IdentifierAdapter();

    @Override
    public void write(JsonWriter jsonWriter, ItemsGrid itemsGrid) {
        throw new UnsupportedOperationException("Writing not supported at this time");
    }

    @Override
    public ItemsGrid read(JsonReader jsonReader) throws IOException {
        switch(jsonReader.peek()) {
            case NULL -> {
                jsonReader.nextNull();
                throw new JsonSyntaxException("Null value not allowed");
            }
            case STRING -> {
                var itemIdentifier = this.identifierAdapter.read(jsonReader);
                var gridEntry= new ItemsGrid();
                gridEntry.items = ImmutableList.of(itemIdentifier);
                return gridEntry;
            }
            default -> {
                ItemsGrid gridEntry = new ItemsGrid();
                return resolveJsonData(jsonReader, gridEntry);
            }
        }
    }

    private ItemsGrid resolveJsonData(JsonReader in, ItemsGrid gridEntry) throws IOException {
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "items" -> gridEntry.items = readItems(in);
                case "borderColor" -> gridEntry.borderColor = in.nextInt();
                case "borderWidth" -> gridEntry.borderWidth = in.nextInt();
                case "highlightColor" -> gridEntry.highlightColor = in.nextInt();
                case "isGradientBorder" -> gridEntry.isGradientBorder = in.nextBoolean();
                default -> in.skipValue();
            }
        }
        in.endObject();
        return gridEntry;
    }

    private List<Identifier> readItems(JsonReader in) throws IOException {
        List<Identifier> items = new ArrayList<>();
        in.beginArray();
        while (in.hasNext()) {
            items.add(this.identifierAdapter.read(in));
        }
        in.endArray();
        return items;
    }

}
