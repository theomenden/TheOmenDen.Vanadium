package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.ChatFormatting;;

import java.io.IOException;

public class ChatFormatAdapter extends TypeAdapter<ChatFormatting> {
    @Override
    public void write(JsonWriter jsonWriter, ChatFormatting formatting) throws IOException {
        if(formatting == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(formatting.getName());
        }
    }

    @Override
    public ChatFormatting read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonSyntaxException("Required value, cannot be null");
        }

        String name = jsonReader.nextString();
        return ChatFormatting.getByName(name);
    }
}
