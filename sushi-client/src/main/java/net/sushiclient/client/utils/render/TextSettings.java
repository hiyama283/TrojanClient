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

package net.sushiclient.client.utils.render;

import com.google.gson.annotations.SerializedName;
import net.sushiclient.client.config.data.EspColor;

public class TextSettings {
    @SerializedName("font")
    private String font;
    @SerializedName("color")
    private EspColor color;
    @SerializedName("pts")
    private int pts;
    @SerializedName("shadow")
    private boolean shadow;

    public TextSettings() {
    }

    public TextSettings(String font, EspColor color, int pts, boolean shadow) {
        this.font = font;
        this.color = color;
        this.pts = pts;
        this.shadow = shadow;
    }

    public String getFont() {
        return font;
    }

    public EspColor getColor() {
        return color;
    }

    public int getPts() {
        return pts;
    }

    public boolean hasShadow() {
        return shadow;
    }

    public TextSettings setFont(String font) {
        return new TextSettings(font, color, pts, shadow);
    }

    public TextSettings setColor(EspColor color) {
        return new TextSettings(font, color, pts, shadow);
    }

    public TextSettings setPts(int pts) {
        return new TextSettings(font, color, pts, shadow);
    }

    public TextSettings setShadow(boolean shadow) {
        return new TextSettings(font, color, pts, shadow);
    }
}
