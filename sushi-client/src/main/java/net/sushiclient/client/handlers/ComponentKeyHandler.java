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

package net.sushiclient.client.handlers;

import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.input.KeyPressEvent;
import net.sushiclient.client.events.input.KeyReleaseEvent;
import net.sushiclient.client.gui.ComponentContext;
import net.sushiclient.client.gui.Components;
import net.sushiclient.client.utils.render.GuiUtils;

public class ComponentKeyHandler {

    @EventHandler(timing = EventTiming.PRE, priority = 1500)
    public void onKeyPress(KeyPressEvent e) {
        if (!GuiUtils.isGameLocked()) return;
        ComponentContext<?> topComponent = Components.getTopContext();
        if (topComponent == null) return;
        topComponent.getOrigin().onKeyPressed(e.getKeyCode(), e.getKey());
    }

    @EventHandler(timing = EventTiming.PRE, priority = 1500)
    public void onKeyRelease(KeyReleaseEvent e) {
        if (!GuiUtils.isGameLocked()) return;
        ComponentContext<?> topComponent = Components.getTopContext();
        if (topComponent == null) return;
        topComponent.getOrigin().onKeyReleased(e.getKeyCode());
    }
}
