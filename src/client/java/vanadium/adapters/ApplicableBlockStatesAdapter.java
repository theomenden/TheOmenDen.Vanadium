package vanadium.adapters;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vanadium.VanadiumClient;
import vanadium.models.ApplicableBlockStates;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class ApplicableBlockStatesAdapter extends TypeAdapter<ApplicableBlockStates> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> MODIDS = Set.of(VanadiumClient.MODID, VanadiumClient.COLORMATIC_ID);

    @Override
    public void write(JsonWriter jsonWriter, ApplicableBlockStates applicableBlockStates) throws IOException {
        throw new UnsupportedOperationException("writing is not supported");
    }

    @Override
    public ApplicableBlockStates read(JsonReader jsonReader) throws IOException {
        if(jsonReader.peek() == JsonToken.NULL) {
            throw new JsonSyntaxException("Unexpected null value");
        }
        String s = jsonReader.nextString();
        return fromReadJson(s);
    }

    private static ApplicableBlockStates fromReadJson(String blockDescription) {
        ApplicableBlockStates applicableBlockStates = new ApplicableBlockStates();
        Block block;
        String[] parts = blockDescription.split(":");
        int beginningIndex;
        try {
            if (parts.length <= 1 || parts[1].indexOf('=') >= 0) {
                block = Registries.BLOCK.get(new Identifier(parts[0]));
                beginningIndex = 1;
            } else {
                Identifier identifier = Identifier.of(parts[0], parts[1]);

                if(MODIDS.contains(parts[0])) {
                    initializeSpecialBlockStates(applicableBlockStates, identifier, parts);
                    return applicableBlockStates;
                } else {
                    block = Registries.BLOCK.get(identifier);
                }
                beginningIndex = 2;
            }
        } catch (Exception e) {
            throw new JsonSyntaxException("Invalid block identifier: " + blockDescription, e);
        }

        applicableBlockStates.block = block;
        BlockStatePredicate predicate = BlockStatePredicate.forBlock(block);

        for(int i = beginningIndex; i < parts.length; i++) {
            int splitIndex = parts[i].indexOf('=');
            if(splitIndex < 0) {
                throw new JsonSyntaxException("Invalid property syntax: " + parts[i]);
            }
            String propertyName = parts[i].substring(0, splitIndex);

            Property<?> propertyState = block
                    .getDefaultState()
                    .getProperties()
                    .stream()
                    .filter(readableProperty -> readableProperty.getName().equals(propertyName))
                    .findFirst()
                    .orElse(null);

            if(propertyState == null) {
                throw new JsonSyntaxException("Invalid property: " + propertyName);
            }

            String[] propertyValues = parts[i].substring(splitIndex + 1).split(",");
            List<Comparable<?>> container = new ArrayList<>();

            Arrays
                    .stream(propertyValues)
                    .forEach(s -> putPropertyValue(container, propertyState, s));

            predicate = predicate.with(propertyState, container::contains);
        }

        applicableBlockStates.states = new ArrayList<>();
        boolean isExcluded = false;

        for(BlockState state: block.getStateManager()
                                   .getStates()) {
            if(predicate.test(state)) {
                applicableBlockStates.states.add(state);
            } else {
                isExcluded = true;
            }
        }

        if(!isExcluded) {
            applicableBlockStates.states.clear();
        }
        return applicableBlockStates;
    }

    private static void initializeSpecialBlockStates(ApplicableBlockStates states, Identifier identifier, String[] parts) {
        states.specialKey = identifier;

        if(parts.length != 3) {
            LOGGER.warn("Special identifier does not specify a sole property: {}", Arrays.toString(parts));
        } else{
            IntStream
                    .range(2, parts.length)
                    .forEach(i -> {
                        int split = parts[i].indexOf('=');
                        if (split < 0) {
                            throw new JsonSyntaxException("Invalid property syntax: " + parts[i]);
                        }
                        String[] propertyValues = parts[i]
                                .substring(split + 1)
                                .split(",");
                        Arrays
                                .stream(propertyValues)
                                .forEach(propertyValue -> {
                                    Identifier value = Identifier.tryParse(propertyValue.replaceFirst("/", ":"));

                                    if (value == null) {
                                        throw new JsonSyntaxException("Invalid identifier value: " + propertyValue);
                                    }

                                    states.specialIds.add(value);
                                });
                    });
        }
    }

    private static <T extends Comparable<T>> void putPropertyValue(List<? super T> container, Property<T> propertyState, String propertyValue) {
        Optional<T> value = propertyState.parse(propertyValue);

        value.ifPresentOrElse(container::add, () -> {
            throw new JsonSyntaxException("Invalid property value: " + propertyValue);});
    }
}
