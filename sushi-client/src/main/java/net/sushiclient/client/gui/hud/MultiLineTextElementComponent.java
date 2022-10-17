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

package net.sushiclient.client.gui.hud;

import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class MultiLineTextElementComponent extends BaseHudElementComponent {
    public MultiLineTextElementComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public void onRender() {
        double x = getWindowX() + 1;
        double y = getWindowY() + 1;
        List<Double> width = new ArrayList<>();
        double height = 0;

        for (String s : getText()) {
            TextPreview preview = GuiUtils.prepareText(s, getTextSettings("text").getValue());
            preview.draw(x, y);
            width.add(preview.getWidth() + 3);
            height += preview.getHeight() + 4;
            y += preview.getHeight() + 1;
        }

        Collections.sort(width);
        Collections.reverse(width);

        try {
            setWidth(width.get(0));
            setHeight(height);
        } catch (IndexOutOfBoundsException e) {
            setWidth(10);
            setHeight(10);
        }
    }

    abstract protected String[] getText();
}
