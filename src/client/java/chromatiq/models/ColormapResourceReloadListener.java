package chromatiq.models;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ColormapResourceReloadListener  extends SimpleResourceReloadListener<int[]> {
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
    public CompletableFuture apply(int[] data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> colorMapping = data, executor);
    }

    public boolean hasACustomColorMapping() {
        return colorMapping != null;
    }
}
