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

import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.utils.player.InventoryType;
import net.sushiclient.client.utils.player.InventoryUtils;
import net.sushiclient.client.utils.player.ItemSlot;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

public abstract class BaseItemCounterComponent extends BaseHudElementComponent implements ItemCounterComponent {
    private final Configuration<Boolean> textMode;
    private final Configuration<DoubleRange> scale;

    public BaseItemCounterComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        textMode = getConfiguration("text_mode", "Text mode", null, Boolean.class, false);
        scale = getConfiguration("scale", "Scale", null, DoubleRange.class, new DoubleRange(1, 16, 0.7, 0.1, 1));
    }

    @Override
    public void onRender() {
        if (textMode.getValue()) {
            ItemSlot[] item = InventoryUtils.findItemSlots(targetItem(), null, InventoryType.values());
            int count = 0;
            for (ItemSlot itemSlot : item) {
                count += itemSlot.getItemStack().getCount();
            }

            TextPreview preview = GuiUtils.prepareText(targetItem().getDefaultInstance().getDisplayName() + "[" +
                    count + "]", getTextSettings("text").getValue());
            preview.draw(getWindowX() + 1, getWindowY() + 1);
            setWidth(preview.getWidth() + 3);
            setHeight(preview.getHeight() + 4);
        } else {
            renderItem(targetItem(), true);
        }
    }

    @Override
    public void onRelocate() {
        double MARGIN = 10;
        double base = scale.getValue().getCurrent() + MARGIN;

        if (textMode.getValue()) {
            ItemSlot[] item = InventoryUtils.findItemSlots(targetItem(), null, InventoryType.values());
            int count = 0;
            for (ItemSlot itemSlot : item) {
                count += itemSlot.getItemStack().getCount();
            }

            TextPreview text = GuiUtils.prepareText(targetItem().getDefaultInstance().getDisplayName() + "[" +
                    count + "]", getTextSettings("text").getValue());
            text.draw(getWindowX(), getWindowY());
            setWidth(text.getWidth());
            setHeight(text.getHeight());
        } else {
            setWidth(base);
            setHeight(base);
        }
    }
}
