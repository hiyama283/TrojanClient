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

import net.sushiclient.client.Sushi;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.WorldLoadEvent;

public class ConfigurationHandler {

    @EventHandler(timing = EventTiming.PRE)
    public void onLoadWorld(WorldLoadEvent e) {
        if (e.getClient() == null) {
            Sushi.getProfile().getModules().disable();
            Sushi.getProfile().save();
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onPostWorldLoad(WorldLoadEvent e) {
        if (e.getClient() != null) {
            Sushi.getProfile().getModules().enable();
        }
    }
}
