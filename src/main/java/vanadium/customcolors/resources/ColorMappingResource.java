package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ColorMappingResource implements SimpleResourceReloadListener {
    private static final Logger logger = LogManager.getLogger();
    private final Identifier identifier;
    private final Identifier optifineIdentifier;
    protected int[] colorMapping = null;

    public ColorMappingResource(Identifier identifier) {
        this.identifier = identifier;
        this.optifineIdentifier = new Identifier("minecraft", "optifine/" + identifier.getPath());
    }

    @Override
    public String getName() {
        return SimpleResourceReloadListener.super.getName();
    }

    @Override
    public Identifier getFabricId() {
        return identifier;
    }

    @Override
    public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {

            try {
                RawTextureDataLoader.loadRawTextureData(manager, identifier);
            }
            catch(IOException e) {
                logger.error("Failed to load color mapping from path, attempting Optifine directory", e);
                attemptToLoadFromOptifineDirectory(manager);
            }
            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Object data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> colorMapping = (int[]) data, executor);
    }

    public boolean hasCustomColorMapping() {
        return colorMapping != null;
    }

    private void attemptToLoadFromOptifineDirectory(ResourceManager manager) {
        try {
            RawTextureDataLoader.loadRawTextureData(manager, optifineIdentifier);
        } catch (IOException e) {
            logger.error("Failed to load color mapping from Optifine directory", e);
        }
    }

}