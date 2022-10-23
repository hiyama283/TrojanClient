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

package net.sushiclient.client.gui.hud.elements;

import net.sushiclient.client.ModInformation;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.hud.BaseHudElementComponent;
import net.sushiclient.client.gui.hud.TextElementComponent;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;
import net.sushiclient.client.utils.render.TextSettings;

import java.awt.*;

public class WatermarkComponent extends BaseHudElementComponent {
    private final Configuration<String> font;
    private final Configuration<EspColor> color;
    private final Configuration<IntRange> pts;
    private final Configuration<Boolean> shadow;
    public WatermarkComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        font = getConfiguration("font", "Font", null, String.class, "FoughtKnight");
        color = getConfiguration("color", "Color", null, EspColor.class,
                new EspColor(new Color(255, 255, 255), false, false));
        pts = getConfiguration("pts", "pts", null, IntRange.class,
                new IntRange(9, 15, 1, 1));
        shadow = getConfiguration("shadow", "Shadow", null, Boolean.class, true);
    }

    @Override
    public void onRender() {
        TextPreview preview = GuiUtils.prepareText(ModInformation.name + " - " + ModInformation.version,
                new TextSettings(font.getValue(), color.getValue(), pts.getValue().getCurrent(), shadow.getValue()));
        preview.draw(getWindowX() + 1, getWindowY() + 1);
        setWidth(preview.getWidth() + 3);
        setHeight(preview.getHeight() + 4);
    }


    @Override
    public String getId() {
        return "watermark";
    }

    @Override
    public String getName() {
        return "Watermark";
    }
}
