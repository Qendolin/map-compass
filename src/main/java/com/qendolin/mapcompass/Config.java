package com.qendolin.mapcompass;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

public class Config {
    public boolean enabled = true;
    public CompassSide side = CompassSide.AUTOMATIC;

    public boolean reverseEW = false;

    public CompassSize size = CompassSize.AUTOMATIC;

    public CompassOffset offsetDirection = CompassOffset.DOWN_OUT;

    public int offsetX = 0;

    public int offsetY = 0;

    public enum CompassOffset {
        NONE(0, 0), DOWN(0, 1), DOWN_OUT(-1, 1), OUT(-1, 0), UP_OUT(-1, -1),
        UP(0, -1), UP_IN(1, -1), IN(1, 0), DOWn_IN(1, 1);

        public final Vec2f vec;

        CompassOffset(float fx, float fy) {
            this.vec = new Vec2f(fx, fy);
        }
    }

    public enum CompassSize {
        AUTOMATIC(0), SMALL(1f), MEDIUM(1.5f), LARGE(2f);

        public final float scale;

        CompassSize(float scale) {
            this.scale = scale;
        }

    }

    public enum CompassSide {
        AUTOMATIC, LEFT, RIGHT, HIDDEN;
    }
}
