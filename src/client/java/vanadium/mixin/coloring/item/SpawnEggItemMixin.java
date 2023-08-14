package vanadium.mixin.coloring.item;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.Vanadium;

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin extends Item {

    @Shadow @Final private EntityType<?> type;

    private SpawnEggItemMixin() {
        super(null);
    }

    @Inject(method="getColor",
    at = @At("HEAD"),
    cancellable = true)
    private void onGetColor(int tintIdx, CallbackInfoReturnable<Integer> cir) {
        if(tintIdx > 1) {
            tintIdx = 1;
        }
        int color = Vanadium.COLOR_PROPERTIES
                .getProperties()
                .getSpawnEgg(type, tintIdx);

        if(color != 0) {
            cir.setReturnValue(color);
        }
    }
}
