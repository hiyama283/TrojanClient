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

package net.sushiclient.client.gui.hud.elements.counter;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.BaseItemCounterComponent;

public class ObsidianCountComponent extends BaseItemCounterComponent {
    public ObsidianCountComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    public Item targetItem() {
        return Item.getItemFromBlock(Blocks.OBSIDIAN);
    }

    @Override
    public String getId() {
        return "obsidian_count_component";
    }

    @Override
    public String getName() {
        return "ObsidianCountComponent";
    }
}
