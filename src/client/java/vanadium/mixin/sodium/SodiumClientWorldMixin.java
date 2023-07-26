package vanadium.mixin.sodium;

import me.jellysquid.mods.sodium.client.world.BiomeSeedProvider;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientLevel.class)
public abstract class SodiumClientWorldMixin extends Level implements BiomeSeedProvider {

    private SodiumClientWorldMixin() {
        super(null, null, null, null, null, false, false, 0L, 0);
    }

    public long getBiomeSeed() {
        return ((SodiumBiomeAccessMixin)this.getBiomeManager()).getBiomeZoomSeed();
    }
}
