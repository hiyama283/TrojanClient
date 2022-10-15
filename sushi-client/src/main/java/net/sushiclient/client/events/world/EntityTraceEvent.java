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

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.sushiclient.client.events.BaseEvent;
import net.sushiclient.client.events.EventTiming;

import java.util.ArrayList;
import java.util.List;

public class EntityTraceEvent extends BaseEvent implements WorldEvent {

    private final World world;
    private final List<Entity> entities;

    public EntityTraceEvent(World world, List<Entity> entities) {
        super(EventTiming.PRE);
        this.world = world;
        this.entities = new ArrayList<>(entities);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    @Override
    public World getWorld() {
        return world;
    }
}
