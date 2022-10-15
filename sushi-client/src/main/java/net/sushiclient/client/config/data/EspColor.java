/*
 * Contact github.com/hiyama283
 * Project "sushi-client"
 *
 * Copyright 2022 hiyama283
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sushiclient.client.config.data;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.*;

public class EspColor {
    @SerializedName("color")
    private final Color color;
    @SerializedName("rainbow")
    private final boolean rainbow;
    @SerializedName("alpha_enabled")
    private final boolean alphaEnabled;

    public EspColor(Color color, boolean rainbow, boolean alphaEnabled) {
        this.color = color;
        this.rainbow = rainbow;
        this.alphaEnabled = alphaEnabled;
    }

    public Color getColor() {
        return color;
    }

    public Color getColor(double y) {
        Color result = getCurrentColor();
        if (isRainbow()) {
            double h = System.currentTimeMillis() / 10000D - System.currentTimeMillis() / 10000;
            result = Color.getHSBColor((float) (y / GuiUtils.getWindowHeight() + h), 1, 1);
            result = new Color(result.getRGB() | (getCurrentColor().getAlpha() & 0xFF), true);
        }
        return result;
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public boolean isAlphaEnabled() {
        return alphaEnabled;
    }

    public Color getCurrentColor() {
        int red, green, blue, alpha;
        if (alphaEnabled) alpha = color.getAlpha();
        else alpha = 255;

        if (rainbow) {
            double h = System.currentTimeMillis() / 10000D - System.currentTimeMillis() / 10000;
            Color color = Color.getHSBColor((float) h, 1, 1);
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
        } else {
            red = color.getRed();
            green = color.getGreen();
            blue = color.getBlue();
        }
        return new Color(red, green, blue, alpha);
    }

    public EspColor setColor(Color color) {
        return new EspColor(color, rainbow, alphaEnabled);
    }

    public EspColor setRainbow(boolean rainbow) {
        return new EspColor(color, rainbow, alphaEnabled);
    }

    public EspColor setAlphaEnabled(boolean alphaEnabled) {
        return new EspColor(color, rainbow, alphaEnabled);
    }

    public EspColor setAlpha(int alpha) {
        return setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EspColor espColor = (EspColor) o;

        if (rainbow != espColor.rainbow) return false;
        if (alphaEnabled != espColor.alphaEnabled) return false;
        return color != null ? color.equals(espColor.color) : espColor.color == null;
    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + (rainbow ? 1 : 0);
        result = 31 * result + (alphaEnabled ? 1 : 0);
        return result;
    }
}
