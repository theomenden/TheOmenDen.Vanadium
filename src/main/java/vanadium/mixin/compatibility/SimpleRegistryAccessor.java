package vanadium.mixin.compatibility;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleRegistry.class)
public interface SimpleRegistryAccessor<T> {
    @Accessor
    @Final
    ObjectList<RegistryEntry.Reference<T>> getRawIdToEntry();
    
    @Accessor
    @Final
    Object2IntMap<T> getEntryToRawId();
}
