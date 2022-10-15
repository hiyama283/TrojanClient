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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class BlockHighlightEvent extends CancellableEvent implements WorldEvent {

    private final RenderGlobal context;
    private final EntityPlayer player;
    private final RayTraceResult target;
    private final int subID;
    private final float partialTicks;

    public BlockHighlightEvent(EventTiming timing, RenderGlobal context, EntityPlayer player, RayTraceResult target, int subID, float partialTicks) {
        super(timing);
        this.context = context;
        this.player = player;
        this.target = target;
        this.subID = subID;
        this.partialTicks = partialTicks;
    }

    public RenderGlobal getContext() {
        return context;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public RayTraceResult getTarget() {
        return target;
    }

    public int getSubID() {
        return subID;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    @Override
    public World getWorld() {
        return player.world;
    }
}
