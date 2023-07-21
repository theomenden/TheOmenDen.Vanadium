package vanadium.models;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.util.RawTextureDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ColormapResourceReloadListener implements SimpleResourceReloadListener<int[]> {
    private final Identifier mapResourceId;
    private final Identifier optifineId;
    protected int[] colorMapping = null;

    public ColormapResourceReloadListener(Identifier mapResourceId) {
        this.mapResourceId = mapResourceId;
        this.optifineId = new Identifier("minecraft", "optifine" + mapResourceId.getPath());
    }

    @Override
    public Identifier getFabricId() {
        return mapResourceId;
    }

    @Override
    public CompletableFuture<int[]> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return RawTextureDataLoader.loadRawTextureData(manager, mapResourceId);
            } catch (IOException e) {
                return tryLoadingFromOptifineDirectory(manager);
            }
        }, executor);
    }

    @Override
    public CompletableFuture apply(int[] data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> colorMapping = data, executor);
    }

    public boolean hasACustomColorMapping() {
        return colorMapping != null;
    }

    @Nullable
    private int[] tryLoadingFromOptifineDirectory(ResourceManager manager) {
        try {
            return RawTextureDataLoader.loadRawTextureData(manager, optifineId);
        } catch (IOException e) {
            return null;
        }
    }
}
