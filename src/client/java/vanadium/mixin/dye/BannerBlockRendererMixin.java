package vanadium.mixin.dye;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vanadium.entry.Vanadium;
import vanadium.util.ColorConverter;

import java.util.List;

@Mixin(BannerRenderer.class)
public abstract class BannerBlockRendererMixin {

    @Redirect(
            method="render(Lnet/minecraft/world/level/block/entity/BannerBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at =@At(
                    value="INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/BannerBlockEntity;getPatterns()Ljava/util/List;"
            )
    )
      private static List<Pair<Holder<BannerPattern>, DyeColor>> proxyBannerColorComponents(BannerBlockEntity instance) {
        var color = Vanadium.COLOR_PROPERTIES.getProperties().getBannerRgb(instance.getBaseColor());

        if(color != null) {
            instance.getPatterns().add(matchColorToBannerPattern(color));
        }

        return instance.getPatterns();
    }

    private static Pair<Holder<BannerPattern>, DyeColor> matchColorToBannerPattern(float[] floats) {

        var color = ColorConverter.convertFloatArrayToIntColor(floats);

        var mapColor =  MapColor.getColorFromPackedId(color);

        var dyeColor = DyeColor.byId(color);

        return Pair.of(BuiltInRegistries.BANNER_PATTERN.getHolderOrThrow(BannerPatterns.BASE), dyeColor);
    }
}
