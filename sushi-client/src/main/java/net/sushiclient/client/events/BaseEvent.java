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

package net.sushiclient.client.events;

import net.minecraft.client.Minecraft;

public class BaseEvent implements Event {

    private final EventTiming timing;
    private final boolean async;

    public BaseEvent(EventTiming timing, boolean async) {
        this.timing = timing;
        this.async = async;
    }

    public BaseEvent(EventTiming timing) {
        this(timing, !Minecraft.getMinecraft().isCallingFromMinecraftThread());
    }

    @Override
    public EventTiming getTiming() {
        return timing;
    }

    @Override
    public boolean isAsync() {
        return async;
    }
}
