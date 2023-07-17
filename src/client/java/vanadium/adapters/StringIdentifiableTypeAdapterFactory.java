package vanadium.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.StringIdentifiable;

import java.io.IOException;

public class StringIdentifiableTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        return null;
    }

    private static class StringIdentifiableTypeAdapter<T extends Enum<T> & StringIdentifiable> extends TypeAdapter<T> {
        private final T[] values;

        public StringIdentifiableTypeAdapter(Class<?> cls) {
            this.values = (T[])cls.getEnumConstants();
        }

        @Override
        public void write(JsonWriter jsonWriter, T value) throws IOException {
            if(value == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(value.asString());
            }
        }

        @Override
        public T read(JsonReader jsonReader) throws IOException {
            if(jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                throw new JsonSyntaxException(new NullPointerException("Required value, cannot be null"));
            }
            String name = jsonReader.nextString();
            for(T value : values) {
                if(value.asString().equals(name)) {
                    return value;
                }
            }
            throw new JsonSyntaxException(new IllegalArgumentException("Unknown value: " + name));
        }
    }
}
