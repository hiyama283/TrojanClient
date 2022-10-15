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

package net.sushiclient.client.gui.theme.simple.config;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class SimpleKeyComponent extends BaseComponent {

    private final ThemeConstants constants;
    private final IntArrayList heldKeys = new IntArrayList();
    private int[] keyCodes;
    private boolean listening;
    private boolean hover;

    public SimpleKeyComponent(ThemeConstants constants, int[] keyCodes) {
        this.constants = constants;
        this.keyCodes = keyCodes;
        setHeight(12);
    }

    @Override
    public void onRender() {
        Color color;
        if (hover) color = constants.unselectedHoverColor.getValue();
        else color = constants.disabledColor.getValue();
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color);
        StringBuilder builder = new StringBuilder();
        if (listening) {
            builder.append("Input keys...");
        } else if (keyCodes.length > 0) {
            builder.append("Keybind: ");
            for (int i = 0; i < keyCodes.length - 1; i++) {
                builder.append(Keyboard.getKeyName(keyCodes[i]));
                builder.append(" + ");
            }
            builder.append(Keyboard.getKeyName(keyCodes[keyCodes.length - 1]));
        } else {
            builder.append("Keybind: NONE");
        }
        GuiUtils.prepareText(builder.toString(), constants.font.getValue(), constants.textColor.getValue(), 9, false)
                .draw(getWindowX() + 1, getWindowY() + 1);
        hover = false;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        listening = true;
        heldKeys.clear();
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END) return;
        listening = true;
        heldKeys.clear();
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        heldKeys.add(keyCode);
        return true;
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        if (!listening) return false;
        int[] keyCodes;
        if (keyCode == Keyboard.KEY_ESCAPE)
            keyCodes = new int[0];
        else
            keyCodes = heldKeys.toIntArray();
        onChange(keyCodes);
        this.keyCodes = keyCodes;
        listening = false;
        return true;
    }

    protected void onChange(int[] newValue) {
    }
}
