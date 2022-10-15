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

import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.BlockHighlightEvent;

public class DrawBlockHighlightHandler {
    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent e) {
        BlockHighlightEvent event = new BlockHighlightEvent(EventTiming.PRE, e.getContext(), e.getPlayer(), e.getTarget(), e.getSubID(), e.getPartialTicks());
        EventHandlers.callEvent(event);
        if (event.isCancelled()) e.setCanceled(true);
    }
}
