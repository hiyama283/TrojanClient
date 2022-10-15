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

package net.sushiclient.client.gui.layout;

import net.sushiclient.client.gui.Component;
import net.sushiclient.client.gui.Insets;
import net.sushiclient.client.gui.PanelComponent;

import java.util.List;

class FlowDownLayout implements Layout {

    private final PanelComponent<?> target;

    FlowDownLayout(PanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        double height = 0;
        double marginBottom = 0;
        for (Component component : getComponents()) {
            if (!component.isVisible()) continue;
            Insets margin = component.getMargin();
            double marginTop = Math.max(marginBottom, margin.getTop());
            component.setParent(target);
            component.setX(margin.getLeft());
            component.setY(height + marginTop);
            component.setWidth(target.getWidth() - margin.getLeft() - margin.getRight());
            component.onRelocate();
            height += component.getHeight() + marginTop;
            marginBottom = margin.getBottom();
        }
        target.setHeight(height + marginBottom);
    }

    List<? extends Component> getComponents() {
        return target;
    }
}
