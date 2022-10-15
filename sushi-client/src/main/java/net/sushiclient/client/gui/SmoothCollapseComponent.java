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

import net.minecraft.util.math.MathHelper;

public class SmoothCollapseComponent<T extends Component> extends CollapseComponent<T> {

    private final double totalMillis;
    private long millis;
    private boolean collapsed = true;

    public SmoothCollapseComponent(T component, CollapseMode mode, double totalMillis) {
        super(component, mode);
        this.totalMillis = totalMillis;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.millis = System.currentTimeMillis();
        this.collapsed = collapsed;
    }

    @Override
    public void onRelocate() {
        double progress = MathHelper.clamp((System.currentTimeMillis() - millis) / totalMillis, 0, 1);
        if (!collapsed) setProgress(progress);
        else setProgress(1 - progress);
        super.onRelocate();
    }
}
