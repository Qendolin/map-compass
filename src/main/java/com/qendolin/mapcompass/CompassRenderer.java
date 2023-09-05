package com.qendolin.mapcompass;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

public class CompassRenderer {
    private static final Identifier MAP_ICONS_TEXTURE = new Identifier(Main.MODID, "textures/map/compass.png");
    private static final RenderLayer MAP_ICONS_RENDER_LAYER = RenderLayer.getText(MAP_ICONS_TEXTURE);
    private static final Text[] CARDINAL_STRINGS = new Text[]{
            Text.translatable("mapcompass.map.cardinal_north"),
            Text.translatable("mapcompass.map.cardinal_east"),
            Text.translatable("mapcompass.map.cardinal_south"),
            Text.translatable("mapcompass.map.cardinal_west")
    };
    private static final Text[] CARDINAL_STRINGS_REVERSE = new Text[]{
            Text.translatable("mapcompass.map.cardinal_north"),
            Text.translatable("mapcompass.map.cardinal_west"),
            Text.translatable("mapcompass.map.cardinal_south"),
            Text.translatable("mapcompass.map.cardinal_east")
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

    public static void drawCompass(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack map) {
        Config config = Main.getConfig();
        if (config.side == Config.CompassSide.HIDDEN) return;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        // Map is being unequipped
        if (player.getStackInHand(Hand.MAIN_HAND) != map && player.getStackInHand(Hand.OFF_HAND) != map) return;

        Vector3f compassPos = new Vector3f(LEFT_COMPASS_POS);
        compassPos.add(config.offsetX, config.offsetY, 0);
        boolean renderOnRight = renderOnRight(player, map);
        if (renderOnRight) {
            compassPos.set(128 - compassPos.x, compassPos.y, compassPos.z);
        }

        float size = getCompassScale();
        Vec2f compassSizeOffset = config.offsetDirection.vec;
        if(renderOnRight) compassSizeOffset = new Vec2f(-compassSizeOffset.x, compassSizeOffset.y);
        compassPos.add(compassSizeOffset.x * (CARDINAL_DISTANCE+1) * (size-1), compassSizeOffset.y * (CARDINAL_DISTANCE+1) * (size-1), 0);

        matrices.push();
        matrices.translate(compassPos.x, compassPos.y, compassPos.z);
        matrices.multiply(new Quaternionf(new AxisAngle4f((float) -Math.toRadians(player.getYaw()), 0, 0, 1)));
        matrices.scale(8.0F * size, 8.0F * size, 3.0F);
        Matrix4f modelMat = matrices.peek().getPositionMatrix();

        VertexConsumer quad = vertexConsumers.getBuffer(MAP_ICONS_RENDER_LAYER);
        quad.vertex(modelMat, -1.0F, 1.0F, 0).color(255, 255, 255, 255).texture(0, 0).light(light).next();
        quad.vertex(modelMat, 1.0F, 1.0F, 0).color(255, 255, 255, 255).texture(1, 0).light(light).next();
        quad.vertex(modelMat, 1.0F, -1.0F, 0).color(255, 255, 255, 255).texture(1, 1).light(light).next();
        quad.vertex(modelMat, -1.0F, -1.0F, 0).color(255, 255, 255, 255).texture(0, 1).light(light).next();
        matrices.pop();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Objects.requireNonNull(textRenderer);
        Text[] cardinals = config.reverseEW ? CARDINAL_STRINGS_REVERSE : CARDINAL_STRINGS;
        for (int i = 0; i < cardinals.length; i++) {
            Text cardinal = cardinals[i];
            Vector3f originOffset = CARDINAL_ORIGIN_OFFSETS[i];
            Vector3f offset = CARDINAL_OFFSETS[i];
            float width = (float) textRenderer.getWidth(cardinal);
            float scale = MathHelper.clamp(6f / width, 0.0F, 1f) * 1 / 3 * size;
            matrices.push();
            float x = compassPos.x + offset.x * CARDINAL_DISTANCE * size;
            float y = compassPos.y + offset.y * CARDINAL_DISTANCE * size;
            matrices.translate(x, y, compassPos.z + offset.z);
            matrices.scale(scale, scale, 1.0F);
            matrices.translate(originOffset.x * width, originOffset.y * textRenderer.fontHeight, 0);
            textRenderer.draw(cardinal, 0f, 0f, 0xffffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
            matrices.pop();
        }
    }

    private static boolean renderOnRight(PlayerEntity player, ItemStack map) {
        Config.CompassSide compassSide = Main.getConfig().side;

        if (compassSide == Config.CompassSide.AUTOMATIC) {
            ItemStack mainItem = player.getStackInHand(Hand.MAIN_HAND);
            boolean isMapInMainHand = mainItem == map;

            if (isMapInMainHand) return player.getMainArm() == Arm.LEFT;
            else return player.getMainArm() == Arm.RIGHT;
        }

        return compassSide == Config.CompassSide.RIGHT;
    }

    private static float getCompassScale() {
        Config.CompassSize size = Main.getConfig().size;

        if(size == Config.CompassSize.AUTOMATIC) {
            double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
            if(scaleFactor < 2) return Config.CompassSize.LARGE.scale;
            if(scaleFactor < 3) return Config.CompassSize.MEDIUM.scale;
            return Config.CompassSize.SMALL.scale;
        }

        return size.scale;
    }
}
