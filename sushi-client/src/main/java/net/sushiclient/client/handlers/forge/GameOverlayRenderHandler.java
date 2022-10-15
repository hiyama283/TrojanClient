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

package net.sushiclient.client.handlers.forge;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.GameOverlayRenderEvent;

public class GameOverlayRenderHandler {

    @SubscribeEvent
    public void onPreGameOverlayRender(RenderGameOverlayEvent.Pre e) {
        GameOverlayRenderEvent event = new GameOverlayRenderEvent(EventTiming.PRE, e.getPartialTicks(), e.getResolution(), e.getType());
        EventHandlers.callEvent(event);
        e.setCanceled(event.isCancelled());
    }

    @SubscribeEvent
    public void onPostGameOverlayRender(RenderGameOverlayEvent.Post e) {
        GameOverlayRenderEvent event = new GameOverlayRenderEvent(EventTiming.POST, e.getPartialTicks(), e.getResolution(), e.getType());
        EventHandlers.callEvent(event);
    }
}
