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

import net.minecraft.util.math.MathHelper;
import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;

import java.awt.*;

abstract public class SimpleBarComponent extends BaseComponent {

    private final ThemeConstants constants;
    private double progress;
    private boolean hover;

    public SimpleBarComponent(ThemeConstants constants, double progress) {
        this.constants = constants;
        this.progress = progress;
        setHeight(12);
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        onChange(progress);
        this.progress = progress;
    }

    @Override
    public void onRender() {
        Color color1;
        Color color2;
        if (hover) {
            color1 = constants.unselectedHoverColor.getValue();
            color2 = constants.selectedHoverColor.getValue();
        } else {
            color1 = constants.disabledColor.getValue();
            color2 = constants.barColor.getValue();
        }
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color1);
        GuiUtils.drawRect(getWindowX(), getWindowY(), (int) (getWidth() * progress), getHeight(), color2);
        hover = false;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        double progress = (x - getWindowX()) / getWidth();
        progress = MathHelper.clamp(progress, 0, 1);
        setProgress(progress);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        onClick(toX, toY, type);
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    protected void onChange(double newProgress) {
    }
}
