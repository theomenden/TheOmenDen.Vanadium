package vanadium.mixin.block;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.entry.Vanadium;

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin extends Item {


    @Shadow @Final private EntityType<?> defaultType;

    private SpawnEggItemMixin() {
        super(null);
    }

    @Inject(method="getColor", at = @At("HEAD"), cancellable = true)
    private void onGetColor(int tintIndex, CallbackInfoReturnable<Integer> cir) {
        if(tintIndex > 1) {
            tintIndex = 1;
        }

        int spawnEggColor = Vanadium.COLOR_PROPERTIES.getProperties().getSpawnEgg(defaultType, tintIndex);

        if(spawnEggColor != 0) {
            cir.setReturnValue(spawnEggColor);
        }
    }
}
