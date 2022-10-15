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

package net.sushiclient.client.gui.base;

import net.sushiclient.client.events.input.ClickType;
import net.sushiclient.client.gui.*;
import net.sushiclient.client.gui.layout.Layout;
import net.sushiclient.client.gui.layout.NullLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BasePanelComponent<T extends Component> extends BaseListComponent<T> implements PanelComponent<T> {

    private Layout layout = new NullLayout(this);

    public BasePanelComponent() {
        super(new ArrayList<>());
    }

    public BasePanelComponent(int x, int y, int width, int height, Anchor anchor, Origin origin, Component parent) {
        super(x, y, width, height, anchor, origin, parent, new ArrayList<>());
    }

    @Override
    public T getFocusedComponent() {
        for (T component : this) {
            if (component.isFocused()) return component;
        }
        return null;
    }

    @Override
    public void setFocusedComponent(T component) {
        forEach(c -> c.setFocused(false));
        component.setFocused(true);
    }

    private void execFocus(Consumer<T> consumer) {
        T focused = getFocusedComponent();
        if (focused != null) {
            consumer.accept(focused);
        }
    }

    @Override
    public T getTopComponent(int x, int y) {
        for (T child : this) {
            if (child.getWindowX() > x) continue;
            if (child.getWindowX() + child.getWidth() < x) continue;
            if (child.getWindowY() > y) continue;
            if (child.getWindowY() + child.getHeight() < y) continue;
            return child;
        }
        return null;
    }

    @Override
    public void onRelocate() {
        getLayout().relocate();
    }

    @Override
    public void onRender() {
        ArrayList<T> clone = new ArrayList<>(this);
        Collections.reverse(clone);
        for (T component : clone) {
            if (component.isVisible())
                component.onRender();
        }
    }

    @Override
    public void onClick(int x, int y, ClickType type) {
        T topComponent = getTopComponent(x, y);
        if (topComponent == null) return;
        setFocusedComponent(topComponent);
        topComponent.onClick(x, y, type);
    }

    @Override
    public void onHover(int x, int y) {
        T topComponent = getTopComponent(x, y);
        if (topComponent == null) return;
        topComponent.onHover(x, y);
    }

    @Override
    public void setFocused(boolean focused) {
        if (!focused) {
            forEach(c -> c.setFocused(false));
        }
        super.setFocused(focused);
    }

    @Override
    public void onHold(int fromX, int fromY, int toX, int toY, ClickType type, MouseStatus status) {
        T from = getTopComponent(fromX, fromY);
        T to = getTopComponent(toX, toY);
        if (!Objects.equals(to, from) && status != MouseStatus.START) {
            if (from != null) {
                from.onHold(fromX, fromY, toX, toY, type, MouseStatus.IN_PROGRESS);
                to = getTopComponent(toX, toY);
                if (!from.equals(to))
                    from.onHold(toX, toY, toX, toY, type, MouseStatus.CANCEL);
            }
            if (to != null && !to.equals(from)) {
                to.onHold(fromX, fromY, toX, toY, type, MouseStatus.START);
                setFocusedComponent(to);
            }
            return;
        }
        if (to == null) return;
        setFocusedComponent(to);
        to.onHold(fromX, fromY, toX, toY, type, status);
    }

    @Override
    public void onScroll(int deltaX, int deltaY) {
        execFocus(c -> c.onScroll(deltaX, deltaY));
    }

    @Override
    public boolean onKeyPressed(int keyCode, char key) {
        AtomicBoolean result = new AtomicBoolean();
        execFocus(c -> result.set(c.onKeyPressed(keyCode, key)));
        if (result.get()) return true;
        else return super.onKeyPressed(keyCode, key);
    }

    @Override
    public boolean onKeyReleased(int keyCode) {
        AtomicBoolean result = new AtomicBoolean();
        execFocus(c -> result.set(c.onKeyReleased(keyCode)));
        if (result.get()) return true;
        else return super.onKeyReleased(keyCode);
    }

    @Override
    public boolean add(T component) {
        component.setContext(getContext());
        return super.add(component);
    }

    @Override
    public void setContext(ComponentContext<?> context) {
        super.setContext(context);
        for (T child : this)
            child.setContext(context);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        for (T child : this) {
            child.setVisible(visible);
        }
    }

    @Override
    public Layout getLayout() {
        return layout;
    }

    @Override
    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
