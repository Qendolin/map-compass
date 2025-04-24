package com.qendolin.mapcompass.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qendolin.mapcompass.CompassRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {

    @Inject(
        method = "renderMap",
        at = @At(
            value = "INVOKE",
            //? if >=1.21.2 {
            /*target = "Lnet/minecraft/client/renderer/MapRenderer;render(Lnet/minecraft/client/renderer/state/MapRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ZI)V",
            *///?} else {
            target = "Lnet/minecraft/client/gui/MapRenderer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/level/saveddata/maps/MapItemSavedData;ZI)V",
            //?}
            shift = At.Shift.AFTER))
    private void afterDraw(PoseStack matrices, MultiBufferSource buffer, int packedLight, ItemStack stack, CallbackInfo ci) {
        CompassRenderer.drawCompass(matrices, buffer, packedLight, stack);
    }

}
