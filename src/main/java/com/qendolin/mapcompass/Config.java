package com.qendolin.mapcompass;

import com.qendolin.mapcompass.config.Entry;
import com.qendolin.mapcompass.config.ModConfig;
import net.minecraft.text.TranslatableText;

public class Config implements ModConfig {
    @Entry.EnumButton()
    public CompassSide side = CompassSide.AUTOMATIC;

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
