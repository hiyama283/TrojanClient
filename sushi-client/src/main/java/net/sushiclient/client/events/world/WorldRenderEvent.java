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

package net.sushiclient.client.events.world;

import net.minecraft.client.renderer.RenderGlobal;
import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

public class WorldRenderEvent extends BaseEvent {
    private final RenderGlobal context;
    private final float partialTicks;

    public WorldRenderEvent(EventTiming timing, RenderGlobal context, float partialTicks) {
        super(timing);
        this.context = context;
        this.partialTicks = partialTicks;
    }

    public RenderGlobal getContext() {
        return context;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
