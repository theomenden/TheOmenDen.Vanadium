package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import vanadium.Vanadium;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ColorMappingResource implements SimpleResourceReloadListener<int[]> {
    private static final Logger logger = LogManager.getLogger();
    private final Identifier identifier;
    private final Identifier optifineIdentifier;
    private final Identifier colormaticIdentifier;
    protected int[] colorMapping = null;

    public ColorMappingResource(Identifier identifier) {
        this.identifier = identifier;
        this.optifineIdentifier = new Identifier("minecraft", "optifine/" + identifier.getPath());
        this.colormaticIdentifier = new Identifier(Vanadium.COLORMATIC_ID, identifier.getPath());
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
    public CompletableFuture<int[]> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            int[] resultingNamespaceLoad;

            try {
                   resultingNamespaceLoad = RawTextureDataLoader.loadRawTextureData(manager, identifier);
            }
            catch(IOException e) {

                resultingNamespaceLoad = attemptToLoadFromColormaticNamespace(manager);

                if(resultingNamespaceLoad == null) {
                    logger.error("Failed to load color mapping from path, attempting Optifine directory", e);
                 resultingNamespaceLoad =  attemptToLoadFromOptifineDirectory(manager);
                }
            }
            return resultingNamespaceLoad;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(int[] data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> colorMapping = data, executor);
    }

    public boolean hasCustomColorMapping() {
        return colorMapping != null;
    }

    @Nullable
    private int[] attemptToLoadFromColormaticNamespace(ResourceManager manager) {
        try{
          return RawTextureDataLoader.loadRawTextureData(manager, colormaticIdentifier);
        }
        catch (IOException e) {
            logger.error("Failed to load color mapping from Colormatic directory", e);
            return null;
        }
    }

    @Nullable
    private int[] attemptToLoadFromOptifineDirectory(ResourceManager manager) {
        try {
            return RawTextureDataLoader.loadRawTextureData(manager, optifineIdentifier);
        } catch (IOException e) {
            logger.error("Failed to load color mapping from Optifine directory", e);
            return null;
        }
    }

}