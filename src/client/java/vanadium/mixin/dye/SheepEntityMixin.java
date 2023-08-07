package vanadium.mixin.dye;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vanadium.entry.Vanadium;

@Mixin(Sheep.class)

public abstract class SheepEntityMixin extends Animal {
    private SheepEntityMixin() {
        super(null, null);
    }

    @Inject(
            method="setColor",
            at = @At("HEAD")
    )
    private static void proxySetColor(DyeColor dyeColor, CallbackInfo ci) {
        float[] rgb = Vanadium.COLOR_PROPERTIES.getProperties().getWoolRgb(dyeColor);
         if(rgb != null) {

         }
    }
}
