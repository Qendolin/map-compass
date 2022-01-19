package com.qendolin.mapcompass;

import com.qendolin.mapcompass.config.Entry;
import com.qendolin.mapcompass.config.ModConfig;
import net.minecraft.text.TranslatableText;

public class Config implements ModConfig {
    @Entry.EnumButton()
    public CompassSide side = CompassSide.AUTOMATIC;

    @Entry.ToggleButton()
    public boolean reverseEW = false;

    @Entry.EnumButton()
    public CompassSize size = CompassSize.AUTOMATIC;

    public enum CompassSize {
        AUTOMATIC(0), SMALL(1f), MEDIUM(1.5f), LARGE(2f);

        private final TranslatableText text;
        public final float scale;

        CompassSize(float scale) {
            this.text = new TranslatableText("text.mapcompass.option.size." + this.name().toLowerCase());
            this.scale = scale;
        }

        @Override
        public String toString() {
            return text.getString();
        }
    }

    public enum CompassSide {
        AUTOMATIC, LEFT, RIGHT, HIDDEN;

        private final TranslatableText text;

        CompassSide() {
            this.text = new TranslatableText("text.mapcompass.option.side." + this.name().toLowerCase());
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
