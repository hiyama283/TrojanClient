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

package net.sushiclient.client.gui.font;

import java.awt.*;

public class FontManager {

    public static CFontRenderer iconFont;
    public static CFontRenderer fontRenderer;
    public static CFontRenderer jelloFont, jelloLargeFont;

    public static void init() {
        iconFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/sushi/font/Icon.ttf", 22f, Font.PLAIN), true, false);
        fontRenderer = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/sushi/font/Comfortaa-Bold.ttf", 18f, Font.PLAIN), true, false);
        jelloFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/sushi/font/JelloLight.ttf", 19, Font.PLAIN), true, false);
        jelloLargeFont = new CFontRenderer(new CFont.CustomFont("/assets/minecraft/sushi/font/JelloLight.ttf", 23f, Font.PLAIN), true, false);
    }

    public static int getWidth(String str) {
        return fontRenderer.getStringWidth(str);
    }

    public static int getHeight() {
        return fontRenderer.getHeight() + 2;
    }

    public static void draw(String str, int x, int y, int color) {
        fontRenderer.drawString(str, x, y, color);
    }

    public static void draw(String str, int x, int y, Color color) {
        fontRenderer.drawString(str, x, y, color.getRGB());
    }

    public static int getIconWidth() {
        return iconFont.getStringWidth("q");
    }

    public static int getIconHeight() {
        return iconFont.getHeight();
    }

    public static void drawIcon(int x, int y, int color) {
        iconFont.drawString("q", x, y, color);
    }

    public static void drawIcon(int x, int y, Color color) {
        iconFont.drawString("q", x, y, color.getRGB());
    }

}
