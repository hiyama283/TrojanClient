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

class FlowRightLayout implements Layout {

    private final PanelComponent<?> target;

    FlowRightLayout(PanelComponent<?> target) {
        this.target = target;
    }

    @Override
    public void relocate() {
        double width = 0;
        double marginRight = 0;
        for (Component component : getComponents()) {
            if (!component.isVisible()) continue;
            Insets margin = component.getMargin();
            double marginLeft = Math.max(marginRight, margin.getLeft());
            component.setParent(target);
            component.setX(width + marginLeft);
            component.setY(margin.getTop());
            component.setHeight(target.getHeight() - margin.getTop() - margin.getBottom());
            component.onRelocate();
            width += component.getHeight() + marginLeft;
            marginRight = margin.getRight();
        }
        target.setWidth(width + marginRight);
    }

    List<? extends Component> getComponents() {
        return target;
    }
}
