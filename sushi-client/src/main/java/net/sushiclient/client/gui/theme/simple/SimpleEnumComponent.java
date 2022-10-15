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

import net.sushiclient.client.config.data.Named;
import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.MouseStatus;
import net.sushiclient.client.gui.base.BaseComponent;
import net.sushiclient.client.gui.theme.ThemeConstants;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class SimpleEnumComponent<T extends Named> extends BaseComponent {

    private final ThemeConstants constants;
    private final String text;
    private final T[] values;
    private final T init;
    private int counter;
    private boolean hover;

    public SimpleEnumComponent(ThemeConstants constants, String text, T init, Class<T> tClass) {
        this.constants = constants;
        this.text = text;
        this.init = init;
        this.values = values(tClass);
        counter = Arrays.asList(values).indexOf(init);
        setHeight(12);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] values(Class<?> c) {
        try {
            return (T[]) c.getMethod("values").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();

            return (T[]) new Named[0];
        }
    }

    @Override
    public void onRender() {
        Color color;
        if (hover) color = constants.unselectedHoverColor.getValue();
        else color = constants.disabledColor.getValue();
        GuiUtils.drawRect(getWindowX(), getWindowY(), getWidth(), getHeight(), color);
        GuiUtils.prepareText(text, constants.font.getValue(), constants.textColor.getValue(), 9, false)
                .draw(getWindowX() + 1, getWindowY() + 1);
        TextPreview preview = GuiUtils.prepareText(getNamed(counter).getName(), constants.font.getValue(), constants.textColor.getValue(), 9, false);
        preview.draw(getWindowX() + getWidth() - preview.getWidth() - 1, getWindowY() + 1);
        hover = false;
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        onChange(getNamed(counter + 1));
        counter++;
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        if (status != MouseStatus.END) return;
        onChange(getNamed(counter + 1));
        counter++;
    }

    @Override
    public void onHover(int x, int y) {
        hover = true;
    }

    private T getNamed(int counter) {
        if (values.length < 1) return init;
        return values[counter % values.length];
    }

    protected void onChange(T newValue) {
    }
}
