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

package net.sushiclient.client.gui;

import java.util.ArrayList;
import java.util.List;

public class Components {

    private static final ArrayList<ComponentContext<?>> components = new ArrayList<>();

    public static ComponentContext<?> getTopContext() {
        if (components.isEmpty()) return null;
        return components.get(0);
    }

    public static void setTopComponent(ComponentContext<?> component) {
        if (!components.remove(component)) return;
        components.add(0, component);
        components.forEach(c -> c.getOrigin().setFocused(false));
        component.getOrigin().setFocused(true);
    }

    public static <T extends Component> ComponentContext<T> show(T component, boolean overlay, boolean close, int index) {
        if (close) closeAll();
        BaseComponentContext<T> context = new BaseComponentContext<>(component, overlay);
        components.add(index, context);
        component.setContext(context);
        component.setVisible(true);
        return context;
    }

    public static <T extends Component> ComponentContext<T> show(T component, boolean overlay, boolean close) {
        return show(component, overlay, close, 0);
    }

    private static void close(ComponentContext<?> component) {
        component.getOrigin().setVisible(false);
        components.remove(component);
    }

    public static void closeAll() {
        new ArrayList<>(components).forEach(Components::close);
    }

    public static List<ComponentContext<?>> getAll() {
        return new ArrayList<>(components);
    }

    private static class BaseComponentContext<T extends Component> implements ComponentContext<T> {
        final T origin;
        final boolean overlay;

        public BaseComponentContext(T origin, boolean overlay) {
            this.origin = origin;
            this.overlay = overlay;
        }

        @Override
        public T getOrigin() {
            return origin;
        }

        @Override
        public boolean isOverlay() {
            return overlay;
        }

        @Override
        public void close() {
            Components.close(this);
        }
    }
}
