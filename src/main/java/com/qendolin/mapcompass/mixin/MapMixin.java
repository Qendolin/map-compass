package com.qendolin.mapcompass.mixin;

import com.qendolin.mapcompass.Config;
import com.qendolin.mapcompass.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(targets = "net.minecraft.client.render.MapRenderer$MapTexture")
public abstract class MapMixin {
	@Shadow private MapState state;
	private static final Identifier MAP_ICONS_TEXTURE = new Identifier(Main.MODID, "textures/map/compass.png");
	private static final RenderLayer MAP_ICONS_RENDER_LAYER = RenderLayer.getText(MAP_ICONS_TEXTURE);
	private static final TranslatableText[] CARDINAL_STRINGS = new TranslatableText[]{
			new TranslatableText("text.mapcompass.map.cardinal_north"),
			new TranslatableText("text.mapcompass.map.cardinal_east"),
			new TranslatableText("text.mapcompass.map.cardinal_south"),
			new TranslatableText("text.mapcompass.map.cardinal_west")
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

	/*
		private static final Field SHOW_ICONS;
		static {
			try {
				SHOW_ICONS = MapState.class.getDeclaredField("showIcons");
				SHOW_ICONS.setAccessible(true);
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}
	 */

	public Vec3f compassPos = new Vec3f(-20, 8, -0.1f);
	public float cardinalDistance = 9;

	@Inject(at = @At("RETURN"), method = "draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ZI)V")
	private void drawCompass(MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean hidePlayerIcons, int light, CallbackInfo ci) {
 		/*
 			Does not work, showIcons is not synchronized.
			try {
				if (!SHOW_ICONS.getBoolean(state)) return;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
        */

		if (hidePlayerIcons) return;
		if (Main.CONFIG.side == Config.CompassSide.HIDDEN) return;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;

		Vec3f compassPos = this.compassPos.copy();
		if (renderOnRight(player)) {
			compassPos.set(128 - compassPos.getX(), compassPos.getY(), compassPos.getZ());
		}

		matrices.push();
		matrices.translate(compassPos.getX(), compassPos.getY(), compassPos.getZ());
		matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-player.getYaw()));
		matrices.scale(8.0F, 8.0F, 3.0F);
		Matrix4f modelMat = matrices.peek().getPositionMatrix();

		VertexConsumer quad = vertexConsumers.getBuffer(MAP_ICONS_RENDER_LAYER);
		quad.vertex(modelMat, -1.0F, 1.0F, 0).color(255, 255, 255, 255).texture(0, 0).light(light).next();
		quad.vertex(modelMat, 1.0F, 1.0F, 0).color(255, 255, 255, 255).texture(1, 0).light(light).next();
		quad.vertex(modelMat, 1.0F, -1.0F, 0).color(255, 255, 255, 255).texture(1, 1).light(light).next();
		quad.vertex(modelMat, -1.0F, -1.0F, 0).color(255, 255, 255, 255).texture(0, 1).light(light).next();
		matrices.pop();

		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		Objects.requireNonNull(textRenderer);
		for (int i = 0; i < CARDINAL_STRINGS.length; i++) {
			Text cardinal = CARDINAL_STRINGS[i];
			Vec3f originOffset = CARDINAL_ORIGIN_OFFSETS[i];
			Vec3f offset = CARDINAL_OFFSETS[i];
			float width = (float) textRenderer.getWidth(cardinal);
			float scale = MathHelper.clamp(6f / width, 0.0F, 1f) * 1 / 3;
			matrices.push();
			float x = compassPos.getX() + offset.getX() * cardinalDistance;
			float y = compassPos.getY() + offset.getY() * cardinalDistance;
			matrices.translate(x, y, compassPos.getZ() + offset.getZ());
			matrices.scale(scale, scale, 1.0F);
			matrices.translate(originOffset.getX() * width, originOffset.getY() * textRenderer.fontHeight, 0);
			textRenderer.draw(cardinal, 0.0F, 0.0F, 0xffffffff, false, matrices.peek().getPositionMatrix(), vertexConsumers, false, 0, light);
			matrices.pop();
		}
	}

	private boolean renderOnRight(PlayerEntity player) {
		Config.CompassSide compassSide = Main.CONFIG.side;

		if (compassSide == Config.CompassSide.AUTOMATIC) {
			boolean isMapInMainHand = false;

			Item mainItem = player.getStackInHand(Hand.MAIN_HAND).getItem();

			if (mainItem instanceof FilledMapItem) {
				Integer mapId = FilledMapItem.getMapId(player.getStackInHand(Hand.MAIN_HAND));
				if (mapId != null) {
					MapState state = FilledMapItem.getMapState(mapId, player.world);
					if (state == this.state) isMapInMainHand = true;
				}
			}

			if (isMapInMainHand) return player.getMainArm() == Arm.LEFT;
			else return player.getMainArm() == Arm.RIGHT;
		}

		return compassSide == Config.CompassSide.RIGHT;
	}
}
