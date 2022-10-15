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

public class LivingLabelRenderEvent extends CancellableEvent {
    private final Entity entityIn;
    private final String str;
    private final double x;
    private final double y;
    private final double z;
    private final int maxDistance;

    public LivingLabelRenderEvent(Entity entityIn, String str, double x, double y, double z, int maxDistance) {
        this.entityIn = entityIn;
        this.str = str;
        this.x = x;
        this.y = y;
        this.z = z;
        this.maxDistance = maxDistance;
    }

    public Entity getEntity() {
        return entityIn;
    }

    public String getStr() {
        return str;
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

    public int getMaxDistance() {
        return maxDistance;
    }
}
