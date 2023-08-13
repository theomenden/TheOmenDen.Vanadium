package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class IdentifierAdapter extends TypeAdapter<Identifier> {

    @Override
    public Identifier read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL) {
            in.nextNull();
            throw new JsonSyntaxException(new NullPointerException("Required nonnull"));
        }
        String id = in.nextString();
        return new Identifier(id);
    }

    @Override
    public void write(JsonWriter out, Identifier value) throws IOException {
        if(value == null) {
            out.nullValue();
        } else {
            out.value(value.toString());
        }
    }
}