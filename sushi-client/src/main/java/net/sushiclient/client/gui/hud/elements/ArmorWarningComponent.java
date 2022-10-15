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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.gui.hud.TextElementComponent;

import java.util.ArrayList;

public class ArmorWarningComponent extends TextElementComponent {
    private final Configuration<DoubleRange> warnPercent;
    public ArmorWarningComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        warnPercent = getConfiguration("warn_percent", "Warn percent", null, DoubleRange.class,
                new DoubleRange(0.5, 1.0, 0.1, 0.1, 1));
    }

    @Override
    protected String getText() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(player.inventory.armorInventory.get(3));
        items.add(player.inventory.armorInventory.get(2));
        items.add(player.inventory.armorInventory.get(1));
        items.add(player.inventory.armorInventory.get(0));

        for (ItemStack item : items) {
            if (!(item.getItem() instanceof ItemArmor)) continue;
            if (item.getMaxDamage() * warnPercent.getValue().getCurrent() < item.getItemDamage())
                return TextFormatting.BOLD + TextFormatting.RED.toString() + "Armor low!" + TextFormatting.RESET;
        }
        return "Armor isnt low.";
    }

    @Override
    public String getId() {
        return "armor_warning_component";
    }

    @Override
    public String getName() {
        return "ArmorWarningComponent";
    }
}
