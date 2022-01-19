package com.qendolin.mapcompass.mixin;

import com.qendolin.mapcompass.CompassRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Inject(
            method = "renderFirstPersonMap",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/MapRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/map/MapState;ZI)V",
                    shift = At.Shift.AFTER))
    private void afterDraw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, CallbackInfo ci) {
        CompassRenderer.drawCompass(matrices, vertexConsumers, light, stack);
    }
}
