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
import net.sushiclient.client.gui.base.BaseSettingComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.*;

abstract public class SimpleToggleComponent<T> extends BaseSettingComponent<T> {

    private final ThemeConstants constants;
    private boolean current;
    private boolean hover;

    public SimpleToggleComponent(ThemeConstants constants, boolean current) {
        this.constants = constants;
        this.current = current;
    }

    public boolean isToggled() {
        return current;
    }

    public void setToggled(boolean current) {
        if (this.current == current) return;
        onChange(current);
        this.current = current;
    }

    @Override
    public void onRender() {
        Color color;
        if (hover) {
            if (current) color = constants.selectedHoverColor.getValue();
            else color = constants.unselectedHoverColor.getValue();
        } else {
            if (current) color = constants.enabledColor.getValue();
            else color = constants.disabledColor.getValue();
        }
        hover = false;
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color);
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        if (type == ClickType.RIGHT) return;
        setToggled(!current);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END || type == ClickType.RIGHT) return;
        setToggled(!current);
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    protected void onChange(boolean newValue) {
    }
}
