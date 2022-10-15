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

package net.sushiclient.client.utils;

import net.minecraft.entity.Entity;

public class EntityInfo<T extends Entity> implements Comparable<EntityInfo<T>> {
    private final T entity;
    private final double distanceSq;

    public EntityInfo(T entity, double distanceSq) {
        this.entity = entity;
        this.distanceSq = distanceSq;
    }

    public T getEntity() {
        return entity;
    }

    public double getDistanceSq() {
        return distanceSq;
    }

    @Override
    public int compareTo(EntityInfo<T> o) {
        return Double.compare(distanceSq, o.distanceSq);
    }

    @Override
    public String toString() {
        return "EntityInfo{" +
                "entity=" + entity +
                ", distanceSq=" + distanceSq +
                '}';
    }
}
