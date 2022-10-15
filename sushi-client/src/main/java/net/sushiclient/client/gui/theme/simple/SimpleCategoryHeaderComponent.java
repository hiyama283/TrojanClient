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

package net.sushiclient.client.gui.theme.simple;

import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.modules.Category;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

public class SimpleCategoryHeaderComponent extends BaseComponent {

    private final ThemeConstants constants;
    private final Component parent;
    private final Category category;
    private int holdX;
    private int holdY;

    public SimpleCategoryHeaderComponent(ThemeConstants constants, Category category, Component parent) {
        this.constants = constants;
        this.category = category;
        this.parent = parent;
        setHeight(16);
    }

    @Override
    public void onRender() {
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), constants.headerColor.getValue());
        TextPreview preview = GuiUtils.prepareText(category.getName(), constants.font.getValue(), constants.textColor.getValue(), 10, true);
        preview.draw(getWindowX() + (getWidth() - preview.getWidth()) / 2 - 1, getWindowY() + (getHeight() - preview.getHeight()) / 2 - 1);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (type != ClickType.LEFT) return;
        if (status == MouseStatus.START) {
            this.holdX = (int) (toX - getWindowX());
            this.holdY = (int) (toY - getWindowY());
            return;
        }
        parent.setWindowX(toX - holdX);
        parent.setWindowY(toY - holdY);
    }
}
