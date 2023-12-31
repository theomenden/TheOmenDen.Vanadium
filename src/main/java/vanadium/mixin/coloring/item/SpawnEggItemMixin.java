package vanadium.mixin.coloring.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanadium.utils.VanadiumColormaticResolution;

@Mixin(SpawnEggItem.class)
public abstract class SpawnEggItemMixin extends Item {

    @Shadow @Final private EntityType<?> defaultType;

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
        var colorProperties = ObjectUtils.firstNonNull(
                VanadiumColormaticResolution.COLORMATIC_COLOR_PROPERTIES,
                VanadiumColormaticResolution.COLOR_PROPERTIES
        );
        int color = colorProperties
                .getProperties()
                .getSpawnEgg(defaultType, tintIdx);

        if(color != 0) {
            cir.setReturnValue(color);
        }
    }
}
