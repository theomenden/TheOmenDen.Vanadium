package vanadium.resources;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ColorMappingResource implements SimpleResourceReloadListener {
    private static final Logger logger = LogManager.getLogger();
    private final ResourceLocation identifier;
    private final ResourceLocation optifineIdentifier;
    protected int[] colorMapping = null;

    public ColorMappingResource(ResourceLocation identifier) {
        this.identifier = identifier;
        this.optifineIdentifier = new ResourceLocation("minecraft", "optifine/" + identifier.getPath());
    }

    @Override
    public String getName() {
        return SimpleResourceReloadListener.super.getName();
    }

    @Override
    public ResourceLocation getFabricId() {
        return identifier;
    }

    @Override
    public CompletableFuture<Void> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                SimpleTexture simpleTexture = new SimpleTexture(identifier);
                simpleTexture.load(manager);
            }
            catch(IOException e) {
                logger.error("Failed to load color mapping from path, attempting Optifine directory", e);
                attemptToLoadFromOptifineDirectory(manager);
            }
            return null;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Object data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> colorMapping = (int[]) data, executor);
    }

    public boolean hasCustomColorMapping() {
        return colorMapping != null;
    }

    private void attemptToLoadFromOptifineDirectory(ResourceManager manager) {
        try {
            SimpleTexture simpleTexture = new SimpleTexture(optifineIdentifier);
            simpleTexture.load(manager);
        } catch (IOException e) {
            logger.error("Failed to load color mapping from Optifine directory", e);
        }
    }

}
