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

package net.sushiclient.client.events.render;

import net.minecraft.entity.Entity;
import net.sushiclient.client.events.CancellableEvent;
import net.sushiclient.client.events.EventTiming;

public class EntityRenderEvent extends CancellableEvent {
    private final boolean model;
    private final Entity entityIn;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float partialTicks;
    private final boolean debug;

    public EntityRenderEvent(EventTiming timing, boolean model, Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean debug) {
        super(timing);
        this.model = model;
        this.entityIn = entityIn;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.partialTicks = partialTicks;
        this.debug = debug;
    }

    public boolean isModel() {
        return model;
    }

    public Entity getEntityIn() {
        return entityIn;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public boolean isDebug() {
        return debug;
    }
}
