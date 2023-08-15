package vanadium.customcolors.mapping;

import com.google.gson.JsonParseException;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vanadium.utils.GsonUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public record LightMappingProperties() {
    private static final Logger LOGGER = LoggerFactory.getLogger("vanadium");

    private LightMappingProperties(LightmapSettings settings) {
        this();
    }

    public static LightMappingProperties loadPropertiesForIdentifier(ResourceManager manager, Identifier identifier) {
        LightmapSettings settings;
        try(Reader reader = new InputStreamReader(manager.getResourceOrThrow(identifier).getInputStream())) {
            settings = GsonUtils.PROPERTY_GSON.fromJson(reader, LightmapSettings.class);
        }catch(JsonParseException e) {
            LOGGER.error("Failed to load lightmapping settings for {}: {}", identifier, e.getMessage());
            settings = new LightmapSettings();
        } catch (IOException e) {
            settings = new LightmapSettings();
        }

        return new LightMappingProperties(settings);

    }

    private static class LightmapSettings {
    }
}