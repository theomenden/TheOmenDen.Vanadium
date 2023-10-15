package vanadium.customcolors.resources;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import vanadium.Vanadium;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ColorMappingResource implements SimpleResourceReloadListener<int[]> {
    private static final Logger logger = LogManager.getLogger();
    private final ResourceLocation identifier;
    private final ResourceLocation optifineIdentifier;
    private final ResourceLocation colormaticIdentifier;
    protected int[] colorMapping = null;

    public ColorMappingResource(ResourceLocation identifier) {
        this.identifier = identifier;
        this.optifineIdentifier = new ResourceLocation("minecraft", "optifine/" + identifier.getPath());
        this.colormaticIdentifier = new ResourceLocation(Vanadium.COLORMATIC_ID, identifier.getPath());
    }

    @Override
    public String getName() {
        return SimpleResourceReloadListener.super.getName();
    }

    @Override
    public ResourceLocation getFabricId() {
        return identifier;
    }


    public boolean hasCustomColorMapping() {
        return colorMapping != null;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    private int[] attemptToLoadFromColormaticNamespace(ResourceManager manager) {
        try{
          return LegacyStuffWrapper.getPixels(manager, colormaticIdentifier);
        }
        catch (IOException e) {
            return null;
        }
    }
    @SuppressWarnings("deprecation")
    private int @Nullable [] attemptToLoadFromOptifineDirectory(ResourceManager manager) {
        try {

            return LegacyStuffWrapper.getPixels(manager, optifineIdentifier);
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public CompletableFuture<int[]> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            int[] resultingNamespaceLoad;

            try {
                resultingNamespaceLoad = attemptToLoadFromColormaticNamespace(manager);
                if(resultingNamespaceLoad == null) {
                    resultingNamespaceLoad = LegacyStuffWrapper.getPixels(manager, identifier);
                }
            }
            catch(IOException e) {
                resultingNamespaceLoad =  attemptToLoadFromOptifineDirectory(manager);
            }
            return resultingNamespaceLoad;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(int[] data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> colorMapping = data, executor);
    }
}