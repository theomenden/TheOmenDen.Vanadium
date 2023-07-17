package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class ChatFormatAdapter extends TypeAdapter<Formatting> {
    @Override
    public void write(JsonWriter jsonWriter, Formatting formatting) throws IOException {
        if(formatting == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(formatting.getName());
        }
    }

    @Override
    public Formatting read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonSyntaxException("Required value, cannot be null");
        }

        String name = jsonReader.nextString();
        return Formatting.byName(name);
    }
}
