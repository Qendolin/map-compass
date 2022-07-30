package com.qendolin.mapcompass;

import com.qendolin.mapcompass.config.Entry;
import com.qendolin.mapcompass.config.ModConfig;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

public class Config implements ModConfig {
    @Entry.EnumButton()
    public CompassSide side = CompassSide.AUTOMATIC;

    @Entry.ToggleButton()
    public boolean reverseEW = false;

    @Entry.EnumButton()
    public CompassSize size = CompassSize.AUTOMATIC;

    @Entry.EnumButton()
    public CompassOffset offsetDirection = CompassOffset.DOWN_OUT;

    @Entry.IntRange(min = -22, max = 84)
    public int offsetX = 0;
    
    @Entry.IntRange(min = -36, max = 148)
    public int offsetY = 0;

    public enum CompassOffset {
        NONE(0, 0), DOWN(0, 1), DOWN_OUT(-1, 1), OUT(-1, 0), UP_OUT(-1, -1),
        UP(0, -1), UP_IN(1, -1), IN(1, 0), DOWn_IN(1, 1);

        private final Text text;
        public final Vec2f vec;

        CompassOffset(float fx, float fy) {
            this.text = Text.translatable("text.mapcompass.option.offset." + this.name().toLowerCase());
            this.vec = new Vec2f(fx, fy);
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    public enum CompassSize {
        AUTOMATIC(0), SMALL(1f), MEDIUM(1.5f), LARGE(2f);

        private final Text text;
        public final float scale;

        CompassSize(float scale) {
            this.text = Text.translatable("text.mapcompass.option.size." + this.name().toLowerCase());
            this.scale = scale;
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    public enum CompassSide {
        AUTOMATIC, LEFT, RIGHT, HIDDEN;

        private final Text text;

        CompassSide() {
            this.text = Text.translatable("text.mapcompass.option.side." + this.name().toLowerCase());
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    @Override
    public String getId() {
        return Main.MODID;
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
