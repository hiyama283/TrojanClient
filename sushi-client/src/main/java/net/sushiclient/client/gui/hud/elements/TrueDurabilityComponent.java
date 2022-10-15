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

package net.sushiclient.client.gui.hud.elements;

import net.minecraft.item.ItemStack;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.TextElementComponent;
import net.sushiclient.client.utils.player.ItemSlot;

public class TrueDurabilityComponent extends TextElementComponent {
    public TrueDurabilityComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
    }

    @Override
    protected String getText() {
        ItemStack item = ItemSlot.current().getItemStack();

        if ((item.getMaxDamage() - item.getItemDamage()) == -1) return item.getDisplayName() + "[Infinite]";
        else if ((item.getMaxDamage() + 1 - item.getItemDamage()) == 1 && item.getMaxDamage() == 1) return item.getDisplayName() + "[Infinite]";
        else return item.getDisplayName() + "[" + (item.getMaxDamage() + 1 - item.getItemDamage()) + "/" + (item.getMaxDamage() + 1) + "]";
    }

    @Override
    public String getId() {
        return "true_durability_component";
    }

    @Override
    public String getName() {
        return "TrueDurabilityComponent";
    }
}
