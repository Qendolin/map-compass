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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

public class CompassRenderer {
    private static final Identifier MAP_ICONS_TEXTURE = new Identifier(Main.MODID, "textures/map/compass.png");
    private static final RenderLayer MAP_ICONS_RENDER_LAYER = RenderLayer.getText(MAP_ICONS_TEXTURE);
    private static final TranslatableText[] CARDINAL_STRINGS = new TranslatableText[]{
            new TranslatableText("text.mapcompass.map.cardinal_north"),
            new TranslatableText("text.mapcompass.map.cardinal_east"),
            new TranslatableText("text.mapcompass.map.cardinal_south"),
            new TranslatableText("text.mapcompass.map.cardinal_west")
    };
    private static final TranslatableText[] CARDINAL_STRINGS_REVERSE = new TranslatableText[]{
            new TranslatableText("text.mapcompass.map.cardinal_north"),
            new TranslatableText("text.mapcompass.map.cardinal_west"),
            new TranslatableText("text.mapcompass.map.cardinal_south"),
            new TranslatableText("text.mapcompass.map.cardinal_east")
    };
    private static final Vec3f[] CARDINAL_ORIGIN_OFFSETS = new Vec3f[]{
            new Vec3f(-0.5f, -1, 0),
            new Vec3f(0, -0.5f, 0),
            new Vec3f(-0.5f, 0, 0),
            new Vec3f(-1, -0.5f, 0),
    };
    private static final Vec3f[] CARDINAL_OFFSETS = new Vec3f[]{
            new Vec3f(0, -1, -0.1f),
            new Vec3f(1, 0, -0.1f),
            new Vec3f(0, 1, -0.1f),
            new Vec3f(-1, 0, -0.1f),
    };

    private static final Vec3f LEFT_COMPASS_POS = new Vec3f(-20, 8, -0.1f);
    private static final float CARDINAL_DISTANCE = 9;

    public static void drawCompass(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack map) {
        if (Main.CONFIG.side == Config.CompassSide.HIDDEN) return;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        // Map is being unequipped
        if (player.getStackInHand(Hand.MAIN_HAND) != map && player.getStackInHand(Hand.OFF_HAND) != map) return;

        Vec3f compassPos = LEFT_COMPASS_POS.copy();
        compassPos.add(Main.CONFIG.offsetX, Main.CONFIG.offsetY, 0);
        boolean renderOnRight = renderOnRight(player, map);
        if (renderOnRight) {
            compassPos.set(128 - compassPos.getX(), compassPos.getY(), compassPos.getZ());
        }

        float size = getCompassScale();
        Vec2f compassSizeOffset = Main.CONFIG.offsetDirection.vec;
        if(renderOnRight) compassSizeOffset = new Vec2f(-compassSizeOffset.x, compassSizeOffset.y);
        compassPos.add(compassSizeOffset.x * (CARDINAL_DISTANCE+1) * (size-1), compassSizeOffset.y * (CARDINAL_DISTANCE+1) *(size-1), 0);

        matrices.push();
        matrices.translate(compassPos.getX(), compassPos.getY(), compassPos.getZ());
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-player.getYaw()));
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
        TranslatableText[] cardinals = Main.CONFIG.reverseEW ? CARDINAL_STRINGS_REVERSE : CARDINAL_STRINGS;
        for (int i = 0; i < cardinals.length; i++) {
            Text cardinal = cardinals[i];
            Vec3f originOffset = CARDINAL_ORIGIN_OFFSETS[i];
            Vec3f offset = CARDINAL_OFFSETS[i];
            float width = (float) textRenderer.getWidth(cardinal);
            float scale = MathHelper.clamp(6f / width, 0.0F, 1f) * 1 / 3 * size;
            matrices.push();
            float x = compassPos.getX() + offset.getX() * CARDINAL_DISTANCE * size;
            float y = compassPos.getY() + offset.getY() * CARDINAL_DISTANCE * size;
            matrices.translate(x, y, compassPos.getZ() + offset.getZ());
            matrices.scale(scale, scale, 1.0F);
            matrices.translate(originOffset.getX() * width, originOffset.getY() * textRenderer.fontHeight, 0);
            textRenderer.draw(cardinal, 0.0F, 0.0F, 0xffffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
            matrices.pop();
        }
    }

    private static boolean renderOnRight(PlayerEntity player, ItemStack map) {
        Config.CompassSide compassSide = Main.CONFIG.side;

        if (compassSide == Config.CompassSide.AUTOMATIC) {
            ItemStack mainItem = player.getStackInHand(Hand.MAIN_HAND);
            boolean isMapInMainHand = mainItem == map;

            if (isMapInMainHand) return player.getMainArm() == Arm.LEFT;
            else return player.getMainArm() == Arm.RIGHT;
        }

        return compassSide == Config.CompassSide.RIGHT;
    }

    private static float getCompassScale() {
        Config.CompassSize size = Main.CONFIG.size;

        if(size == Config.CompassSize.AUTOMATIC) {
            double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
            if(scaleFactor < 2) return Config.CompassSize.LARGE.scale;
            if(scaleFactor < 3) return Config.CompassSize.MEDIUM.scale;
            return Config.CompassSize.SMALL.scale;
        }

        return size.scale;
    }
}
