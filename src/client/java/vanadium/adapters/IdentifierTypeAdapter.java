package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class IdentifierTypeAdapter extends TypeAdapter<ResourceLocation> {
    @Override
    public void write(JsonWriter jsonWriter, ResourceLocation resourceLocation) throws IOException {
        if(resourceLocation == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(resourceLocation.toString());
        }
    }

    @Override
    public ResourceLocation read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonSyntaxException("Required value, cannot be null");
        }

        return new ResourceLocation(jsonReader.nextString());
    }
}
