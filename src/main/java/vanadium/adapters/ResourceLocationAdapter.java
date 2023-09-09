package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class ResourceLocationAdapter extends TypeAdapter<ResourceLocation> {

    @Override
    public ResourceLocation read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL) {
            in.nextNull();
            throw new JsonSyntaxException(new NullPointerException("Required nonnull"));
        }
        String id = in.nextString();
        return new ResourceLocation(id);
    }

    @Override
    public void write(JsonWriter out, ResourceLocation value) throws IOException {
        if(value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }
}