package vanadium.adapters;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.StringRepresentable;

import java.io.IOException;
import java.util.Arrays;

public class StringIdentifiableTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<? super T> cls = typeToken.getRawType();
        if(cls.isEnum()) {
            Class<?>[] implemented = cls.getInterfaces();

            if (Arrays
                    .stream(implemented)
                    .anyMatch(iface -> iface == StringRepresentable.class)) {
                return (TypeAdapter<T>) new StringIdentifiableTypeAdapter<>(cls);
            }
        }
        return null;
    }

    private static class StringIdentifiableTypeAdapter<T extends Enum<T> & StringRepresentable> extends TypeAdapter<T> {
        private final T[] values;

        public StringIdentifiableTypeAdapter(Class<?> cls) {
            this.values = (T[])cls.getEnumConstants();
        }

        @Override
        public void write(JsonWriter jsonWriter, T value) throws IOException {
            if(value == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(value.getSerializedName());
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
                if(value.getSerializedName().equals(name)) {
                    return value;
                }
            }
            throw new JsonSyntaxException(new IllegalArgumentException("Unknown value: " + name));
        }
    }
}
