package com.qendolin.mapcompass;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qendolin.mapcompass.config.Config;
import com.qendolin.mapcompass.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

public class CompassRenderer {
    private static final ResourceLocation MAP_ICONS_TEXTURE = MapCompassInit.getResourceLocation("textures/map/compass.png");
    private static final RenderType MAP_ICONS_RENDER_LAYER = RenderType.text(MAP_ICONS_TEXTURE);
    private static final Component[] CARDINAL_STRINGS = new Component[]{
            Component.translatable("mapcompass.map.cardinal_north"),
            Component.translatable("mapcompass.map.cardinal_east"),
            Component.translatable("mapcompass.map.cardinal_south"),
            Component.translatable("mapcompass.map.cardinal_west")
    };
    private static final Component[] CARDINAL_STRINGS_REVERSE = new Component[]{
            Component.translatable("mapcompass.map.cardinal_north"),
            Component.translatable("mapcompass.map.cardinal_west"),
            Component.translatable("mapcompass.map.cardinal_south"),
            Component.translatable("mapcompass.map.cardinal_east")
    };
    private static final Vector3f[] CARDINAL_ORIGIN_OFFSETS = new Vector3f[]{
            new Vector3f(-0.5f, -1, 0),
            new Vector3f(0, -0.5f, 0),
            new Vector3f(-0.5f, 0, 0),
            new Vector3f(-1, -0.5f, 0),
    };
    private static final Vector3f[] CARDINAL_OFFSETS = new Vector3f[]{
            new Vector3f(0, -1, -0.1f),
            new Vector3f(1, 0, -0.1f),
            new Vector3f(0, 1, -0.1f),
            new Vector3f(-1, 0, -0.1f),
    };

    private static final Vector3f LEFT_COMPASS_POS = new Vector3f(-20, 8, -0.1f);
    private static final float CARDINAL_DISTANCE = 9;

    public static void drawCompass(PoseStack matrices, MultiBufferSource vertexConsumers, int light, ItemStack map) {
        Config config = ConfigManager.instance();
        if (!config.enabled) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        // Map is being unequipped
        if (player.getItemInHand(InteractionHand.MAIN_HAND) != map && player.getItemInHand(InteractionHand.OFF_HAND) != map) return;

        Vector3f compassPos = new Vector3f(LEFT_COMPASS_POS);
        compassPos.add(config.offsetX, config.offsetY, 0);
        boolean renderOnRight = renderOnRight(player, map);
        if (renderOnRight) {
            compassPos.set(128 - compassPos.x, compassPos.y, compassPos.z);
        }

        float size = getCompassScale();
        Vec2 compassSizeOffset = config.offsetDirection.vec;
        if(renderOnRight) compassSizeOffset = new Vec2(-compassSizeOffset.x, compassSizeOffset.y);
        compassPos.add(compassSizeOffset.x * (CARDINAL_DISTANCE+1) * (size-1), compassSizeOffset.y * (CARDINAL_DISTANCE+1) * (size-1), 0);

        matrices.pushPose();
        matrices.translate(compassPos.x, compassPos.y, compassPos.z);
        matrices.mulPose(new Quaternionf(new AxisAngle4f((float) -Math.toRadians(player.getYRot()), 0, 0, 1)));
        matrices.scale(8.0F * size, 8.0F * size, 3.0F);
        Matrix4f modelMat = matrices.last().pose();

        VertexConsumer quad = vertexConsumers.getBuffer(MAP_ICONS_RENDER_LAYER);
        addQuad(quad, modelMat, light);
        matrices.popPose();

        Font font = Minecraft.getInstance().font;
        Objects.requireNonNull(font);
        Component[] cardinals = config.reverseEW ? CARDINAL_STRINGS_REVERSE : CARDINAL_STRINGS;
        for (int i = 0; i < cardinals.length; i++) {
            Component cardinal = cardinals[i];
            Vector3f originOffset = CARDINAL_ORIGIN_OFFSETS[i];
            Vector3f offset = CARDINAL_OFFSETS[i];
            float width = (float) font.width(cardinal);
            float scale = Mth.clamp(6f / width, 0.0F, 1f) * 1 / 3 * size;
            matrices.pushPose();
            float x = compassPos.x + offset.x * CARDINAL_DISTANCE * size;
            float y = compassPos.y + offset.y * CARDINAL_DISTANCE * size;
            matrices.translate(x, y, compassPos.z + offset.z);
            matrices.scale(scale, scale, 1.0F);
            matrices.translate(originOffset.x * width, originOffset.y * font.lineHeight, 0);
            font.drawInBatch(cardinal, 0f, 0f, 0xffffffff, false, matrices.last().pose(), vertexConsumers, Font.DisplayMode.NORMAL, 0, light);
            matrices.popPose();
        }
    }

    private static void addQuad(VertexConsumer vertices, Matrix4f matrix, int light) {
        //? if >1.20.6 {
        /*vertices.addVertex(matrix, -1.0F, 1.0F, 0).setColor(255, 255, 255, 255).setUv(0, 0).setLight(light);
        vertices.addVertex(matrix, 1.0F, 1.0F, 0).setColor(255, 255, 255, 255).setUv(1, 0).setLight(light);
        vertices.addVertex(matrix, 1.0F, -1.0F, 0).setColor(255, 255, 255, 255).setUv(1, 1).setLight(light);
        vertices.addVertex(matrix, -1.0F, -1.0F, 0).setColor(255, 255, 255, 255).setUv(0, 1).setLight(light);
        *///?} else {
        vertices.vertex(matrix, -1.0F, 1.0F, 0).color(255, 255, 255, 255).uv(0, 0).uv2(light).endVertex();
        vertices.vertex(matrix, 1.0F, 1.0F, 0).color(255, 255, 255, 255).uv(1, 0).uv2(light).endVertex();
        vertices.vertex(matrix, 1.0F, -1.0F, 0).color(255, 255, 255, 255).uv(1, 1).uv2(light).endVertex();
        vertices.vertex(matrix, -1.0F, -1.0F, 0).color(255, 255, 255, 255).uv(0, 1).uv2(light).endVertex();
        //?}
    }

    private static boolean renderOnRight(LocalPlayer player, ItemStack map) {
        Config.CompassSide compassSide = ConfigManager.instance().side;

        if (compassSide == Config.CompassSide.AUTOMATIC) {
            ItemStack mainItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            boolean isMapInMainHand = mainItem == map;

            if (isMapInMainHand) return player.getMainArm() == HumanoidArm.LEFT;
            else return player.getMainArm() == HumanoidArm.RIGHT;
        }

        return compassSide == Config.CompassSide.RIGHT;
    }

    private static float getCompassScale() {
        Config.CompassSize size = ConfigManager.instance().size;

        if(size == Config.CompassSize.AUTOMATIC) {
            double scaleFactor = Minecraft.getInstance().getWindow().getGuiScale();
            if(scaleFactor < 2) return Config.CompassSize.SMALL.scale;
            if(scaleFactor < 3) return Config.CompassSize.MEDIUM.scale;
            return Config.CompassSize.LARGE.scale;
        }

        return size.scale;
    }
}
