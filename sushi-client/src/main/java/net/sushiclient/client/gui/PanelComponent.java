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

import net.sushiclient.client.gui.layout.Layout;

public interface PanelComponent<T extends Component> extends ListComponent<T> {
    T getFocusedComponent();

    void setFocusedComponent(T component);

    T getTopComponent(int x, int y);

    Layout getLayout();

    void setLayout(Layout layout);

    default void add(T component, boolean visible) {
        add(component);
        component.setVisible(visible);
    }
}
