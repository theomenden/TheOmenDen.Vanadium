package vanadium.adapters;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import vanadium.models.records.VanadiumColor;
import vanadium.utils.VanadiumColorResource;

import java.io.IOException;

public class VanadiumColorAdapter extends TypeAdapter<VanadiumColor> {
    @Override
    public void write(JsonWriter jsonWriter, VanadiumColor vanadiumColor) throws IOException {
        if(vanadiumColor == null) {
            jsonWriter.nullValue();
        } else {
            String hexValue = VanadiumColorResource.resolveVanadiumColorToHex(vanadiumColor);
            jsonWriter.value(hexValue);
        }
    }

    @Override
    public VanadiumColor read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonParseException(new NullPointerException("Null value"));
        }

        String readColor = jsonReader.nextString().trim();
        try {
            return new VanadiumColor(readColor);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }
}
