package vanadium.mixin.sodium;

import me.jellysquid.mods.sodium.client.world.BiomeSeedProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.class)
public abstract class SodiumClientWorldMixin extends World implements BiomeSeedProvider {
    private SodiumClientWorldMixin() {
        super(
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                0L,
                0
        );
    }

    @Override
    public long sodium$getBiomeSeed() {
        return ((SodiumBiomeAccessAccessor)this.getBiomeAccess()).getSeed();
    }
}
