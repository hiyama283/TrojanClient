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

package net.sushiclient.client.gui.hud;

import net.sushiclient.client.utils.render.GuiUtils;

public class HotbarHudElementComponent extends VirtualHudElementComponent {
    @Override
    public String getId() {
        return "hotbar";
    }

    @Override
    public String getName() {
        return "Hotbar";
    }

    @Override
    public void onRelocate() {
        setWindowX(GuiUtils.getWidth() / 2 - 91);
        setWindowY(GuiUtils.getHeight() - 22);
        setWidth(182);
        setHeight(22);
    }
}
