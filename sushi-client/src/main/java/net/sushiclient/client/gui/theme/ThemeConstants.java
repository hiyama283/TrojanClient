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

package net.sushiclient.client.gui.theme;

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;

import java.awt.*;

public class ThemeConstants {
    public final Configuration<Color> frameColor;
    public final Configuration<Color> headerColor;
    public final Configuration<Color> disabledColor;
    public final Configuration<Color> enabledColor;
    public final Configuration<Color> unselectedHoverColor;
    public final Configuration<Color> selectedHoverColor;
    public final Configuration<Color> textColor;
    public final Configuration<Color> backgroundColor;
    public final Configuration<Color> barColor;
    public final Configuration<Color> barBackgroundColor;
    public final Configuration<Color> textBoxBackgroundColor;
    public final Configuration<Color> textBoxTextColor;
    public final Configuration<Color> outlineColor;
    public final Configuration<Color> menuBarColor;
    public final Configuration<Color> crossMarkColor;
    public final Configuration<Color> crossMarkBackgroundColor;
    public final Configuration<Color> hoverCrossMarkColor;
    public final Configuration<Color> hoverCrossMarkBackgroundColor;
    public final Configuration<Color> selectedCrossMarkColor;
    public final Configuration<Color> selectedCrossMarkBackgroundColor;
    public final Configuration<String> font;

    public ThemeConstants(Configurations c) {
        frameColor = c.get("gui.frame_color", "Frame Color", null, Color.class, new Color(200, 90, 30));
        headerColor = c.get("gui.header_color", "Header Color", null, Color.class, new Color(30, 30, 30));
        disabledColor = c.get("gui.disabled_color", "Disabled Color", null, Color.class, new Color(40, 40, 40));
        enabledColor = c.get("gui.enabled_color", "Enabled Color", null, Color.class, new Color(140, 140, 255));
        unselectedHoverColor = c.get("gui.unselected_hover_color", "Unselected Hover Color", null, Color.class, new Color(30, 30, 30));
        selectedHoverColor = c.get("gui.selected_hover_color", "Selected Hover Color", null, Color.class, new Color(100, 100, 230));
        textColor = c.get("gui.text_color", "Text Color", null, Color.class, new Color(255, 255, 255));
        backgroundColor = c.get("gui.background_color", "Background Color", null, Color.class, new Color(30, 30, 30));
        barColor = c.get("gui.bar_color", "Bar Color", null, Color.class, new Color(140, 140, 255));
        barBackgroundColor = c.get("gui.bar_background_color", "Menu Bar Background Color", null, Color.class, new Color(30, 30, 30));
        textBoxBackgroundColor = c.get("gui.text_box_background_color", "Text Background Color", null, Color.class, new Color(30, 30, 30));
        textBoxTextColor = c.get("gui.text_box_text_color", "Text Color in Text Box", null, Color.class, new Color(30, 30, 30));
        outlineColor = c.get("gui.outline_color", "Outline Color", null, Color.class, new Color(60, 60, 60));
        menuBarColor = c.get("gui.menu_bar_color", "Menu Bar Color", null, Color.class, new Color(30, 30, 30));
        crossMarkColor = c.get("gui.cross_mark_color", "Cross Mark Color", null, Color.class, new Color(50, 40, 40));
        crossMarkBackgroundColor = c.get("gui.cross_mark_background_color", "Cross Mark Background Color", null, Color.class, new Color(50, 40, 40));
        hoverCrossMarkColor = c.get("gui.hover_cross_mark_color", "Hover Cross Mark Color", null, Color.class, new Color(230, 200, 200));
        hoverCrossMarkBackgroundColor = c.get("gui.hover_cross_mark_background_color", "Hover Cross Mark Background Color", null, Color.class, new Color(100, 30, 30));
        selectedCrossMarkColor = c.get("gui.selected_cross_mark_color", "Selected Cross Mark Color", null, Color.class, new Color(230, 200, 200));
        selectedCrossMarkBackgroundColor = c.get("gui.selected_cross_mark_background_color", "Selected Cross Mark Background Color", null, Color.class, new Color(160, 30, 30));

        font = c.get("gui.font", "Font", null, String.class, "Calibri");
    }
}
