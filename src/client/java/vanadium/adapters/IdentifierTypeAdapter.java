package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class IdentifierTypeAdapter extends TypeAdapter<Identifier> {

    @Override
    public void write(JsonWriter jsonWriter, Identifier identifier) throws IOException {
        if(identifier == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(identifier.toString());
        }
    }

    @Override
    public Identifier read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonSyntaxException("Required value, cannot be null");
        }

        return new Identifier(jsonReader.nextString());
    }
}
