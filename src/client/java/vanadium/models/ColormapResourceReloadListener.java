package vanadium.models;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ColormapResourceReloadListener implements SimpleResourceReloadListener<int[]> {
    private final ResourceLocation mapResourceId;
    private final ResourceLocation optifineId;
    protected int[] colorMapping = null;

    public ColormapResourceReloadListener(ResourceLocation mapResourceId) {
        this.mapResourceId = mapResourceId;
        this.optifineId = new ResourceLocation("minecraft", "optifine" + mapResourceId.getPath());
    }

    public boolean hasACustomColorMapping() {
        return colorMapping != null;
    }

    @Override
    public ResourceLocation getFabricId() {
        return mapResourceId;
    }

    @Override
    public CompletableFuture<int[]> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
                var spriteContents =  SpriteLoader.loadSprite(mapResourceId, manager.getResource(mapResourceId).orElse(null));

                return spriteContents.getUniqueFrames().toArray();
        }, executor)
         .exceptionally(e -> tryLoadingFromOptifineDirectory(manager));
    }

    @Override
    public CompletableFuture<Void> apply(int[] data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> colorMapping = data, executor);
    }


    @Nullable
    private int[] tryLoadingFromOptifineDirectory(ResourceManager manager) {
        try {
            return SpriteLoader.loadSprite(optifineId, manager.getResourceOrThrow(optifineId))
                    .getUniqueFrames()
                               .toArray();
        } catch (IOException e) {
            return null;
        }
    }
}
