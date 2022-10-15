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
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.SmoothCollapseComponent;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.*;
import java.util.function.Supplier;

public class SimpleColorPickerHeaderComponent extends BaseComponent {

    private final ThemeConstants constants;
    private final SmoothCollapseComponent<?> component;
    private final Supplier<Color> colorSupplier;
    private final String name;
    private boolean hover;

    public SimpleColorPickerHeaderComponent(ThemeConstants constants, SmoothCollapseComponent<?> component,
                                            Supplier<Color> colorSupplier, String name) {
        this.constants = constants;
        this.component = component;
        this.colorSupplier = colorSupplier;
        this.name = name;
        setHeight(12);
    }

    @Override
    public void onRender() {
        Color color;
        if (hover) {
            if (!component.isCollapsed()) color = constants.selectedHoverColor.getValue();
            else color = constants.unselectedHoverColor.getValue();
        } else {
            if (!component.isCollapsed()) color = constants.enabledColor.getValue();
            else color = constants.disabledColor.getValue();
        }
        hover = false;
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color);
        GuiUtils.prepareText(name, constants.font.getValue(), constants.textColor.getValue(), 9, false)
                .draw(getWindowX() + 1, getWindowY() + 1);
        Color picker = colorSupplier.get();
        double margin = getHeight() - 4;
        GuiUtils.drawRect(getWindowX() + getWidth() - margin - 2, getWindowY() + 2, margin, margin, picker);
        GuiUtils.drawOutline(getWindowX() + getWidth() - margin - 2, getWindowY() + 2, margin, margin, Color.BLACK, 1);
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        component.setCollapsed(!component.isCollapsed());
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END) return;
        component.setCollapsed(!component.isCollapsed());
    }
}
