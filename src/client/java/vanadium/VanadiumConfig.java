package vanadium;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class VanadiumConfig {
    private transient File outputConfigFile;

    public static final VanadiumConfig INSTANCE = new VanadiumConfig();

    private boolean shouldClearSky;
    private boolean shouldClearVoid;
    private boolean shouldBlendSkyLight;
    private boolean shouldFlickerBlockLight;
    private double relativeBlockLightIntensityExponent;

    public VanadiumConfig() {
     shouldClearSky = false;
     shouldClearVoid = false;
     shouldBlendSkyLight = true;
     shouldFlickerBlockLight = true;
     relativeBlockLightIntensityExponent = -13.0;
    }

    public static double getScaledBlockLightIntensity(double relativeBlockLightIntensityExponent) {
        return Math.log(2) * 0.25 * relativeBlockLightIntensityExponent;
    }

    public CompletableFuture<Void> serializeConfigToJsonAsync() {
        return CompletableFuture.runAsync(() -> {
            try(JsonWriter writer = new JsonWriter(new FileWriter(outputConfigFile))) {
                writer.beginObject();
                writer.name("shouldClearSky").value(shouldClearSky);
                writer.name("shouldClearVoid").value(shouldClearVoid);
                writer.name("shouldBlendSkyLight").value(shouldBlendSkyLight);
                writer.name("shouldFlickerBlockLight").value(shouldFlickerBlockLight);
                writer.name("relativeBlockLightIntensityExponent").value(relativeBlockLightIntensityExponent);
                writer.endObject();
            } catch (IOException e) {
                throw new RuntimeException("Serialization failed: " + e.getMessage());
            }
        });
    }

    public static CompletableFuture<VanadiumConfig> deserializeConfigFromJsonAsync() {
        File configFile = new File(FabricLoader
                .getInstance().getConfigDir().toFile(), Vanadium.MODID + ".json");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        return CompletableFuture.supplyAsync(() -> {
            if(!configFile.exists()) {
                VanadiumConfig defaultConfig = VanadiumConfig.INSTANCE;
                defaultConfig.serializeConfigToJsonAsync().join();
                return defaultConfig;
            }

            try(JsonReader reader = new JsonReader(new FileReader(configFile))) {
                reader.beginObject();
                VanadiumConfig config = new VanadiumConfig();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    switch(key) {
                        case "shouldClearSky":
                            config.shouldClearSky = reader.nextBoolean();
                            break;
                        case "shouldClearVoid":
                            config.shouldClearVoid = reader.nextBoolean();
                            break;
                        case "shouldBlendSkyLight":
                            config.shouldBlendSkyLight = reader.nextBoolean();
                            break;
                        case "shouldFlickerBlockLight":
                            config.shouldFlickerBlockLight = reader.nextBoolean();
                            break;
                        case "relativeBlockLightIntensityExponent":
                            config.relativeBlockLightIntensityExponent = reader.nextDouble();
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }
                reader.endObject();
                return config;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
