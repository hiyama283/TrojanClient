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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.gui.hud.MultiLineTextElementComponent;
import net.sushiclient.client.modules.Categories;
import scala.collection.parallel.ParIterableLike;

import java.util.ArrayList;
import java.util.List;

public class TextRaderComponent extends MultiLineTextElementComponent {
    private final Configuration<IntRange> healthGreen;
    private final Configuration<IntRange> healthYellow;
    private final Configuration<IntRange> rangeGreen;
    private final Configuration<IntRange> rangeYellow;
    public TextRaderComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        healthGreen = getConfiguration("health_green", "Health green", null, IntRange.class, new IntRange(12, 20, 0, 1));
        healthYellow = getConfiguration("health_yellow", "Health yellow", null, IntRange.class, new IntRange(6, 20, 0, 1));
        rangeGreen = getConfiguration("range_green", "Range green", null, IntRange.class, new IntRange(40, 60, 0, 1));
        rangeYellow = getConfiguration("range_yellow", "Range yellow", null, IntRange.class, new IntRange(20, 40, 0, 1));
    }

    private int format(double db) {
        return ((int) db);
    }

    @Override
    protected String[] getText() {
        List<String> result = new ArrayList<>();
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        for (EntityPlayer playerEntity : Minecraft.getMinecraft().world.playerEntities) {
            if (playerEntity.getName().equals(player.getName())) continue;

            double health = format(playerEntity.getHealth());
            StringBuilder healthText = new StringBuilder();
            if (health > healthGreen.getValue().getCurrent()) healthText.append(TextFormatting.GREEN);
            else if (health > healthYellow.getValue().getCurrent()) healthText.append(TextFormatting.YELLOW);
            else healthText.append(TextFormatting.RED);

            double range = format(playerEntity.getDistance(player));
            StringBuilder rangeText = new StringBuilder();
            if (range > rangeGreen.getValue().getCurrent()) rangeText.append(TextFormatting.GREEN);
            else if (range > rangeYellow.getValue().getCurrent()) rangeText.append(TextFormatting.YELLOW);
            else rangeText.append(TextFormatting.RED);
            rangeText.append(range).append(TextFormatting.WHITE);

            double fullHealth = format(playerEntity.getHealth() + playerEntity.getAbsorptionAmount());
            String playerNameFormat;
            if (fullHealth > healthGreen.getValue().getCurrent()) playerNameFormat = TextFormatting.GREEN.toString();
            else if (fullHealth > healthYellow.getValue().getCurrent()) playerNameFormat = TextFormatting.YELLOW.toString();
            else playerNameFormat = TextFormatting.RED.toString();

            result.add("[" + rangeText + "] " + playerNameFormat + playerEntity.getName() + TextFormatting.WHITE +
                    " [" + healthText + format(playerEntity.getHealth()) + TextFormatting.YELLOW +
                    "+" + format(playerEntity.getAbsorptionAmount()) + TextFormatting.WHITE + "]");
        }

        return result.toArray(new String[0]);
    }
}
