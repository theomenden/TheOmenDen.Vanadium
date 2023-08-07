package vanadium.util;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vanadium.entry.Vanadium;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Vanadium.MODID);
    private static final Path configurationDirectory = FabricLoader.getInstance()
                                                                   .getConfigDir()
            .resolve("vanadium");

    private static final Path configurationFilepath = configurationDirectory.resolve(Vanadium.MODID + ".json");

    public static boolean isDirectoryReady() {
        if(configurationDirectory.toFile().isDirectory()) {
            return true;
        }
        return makeDirectory();
    }

    public static boolean isFileReady() {
        return configurationFilepath
                .toFile()
                .isFile()
                || createFile();
    }

    public static boolean makeDirectory() {
        return configurationFilepath.toFile().mkdirs();
    }

    public static boolean createFile() {
        try  {
            return configurationFilepath.toFile().createNewFile();
        }
        catch (IOException e) {
            LOGGER.warn("Could not create file {}: {}", configurationFilepath.getFileName(), e.getMessage());
            return false;
        }
    }
}
