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

package net.sushiclient.client.modules;

import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.utils.render.GuiUtils;
import net.sushiclient.client.utils.render.TextPreview;

import java.awt.*;

public interface Category {

    Category COMBAT = new ResourceImageCategory("Combat", "combat.png");
    Category MOVEMENT = new ResourceImageCategory("Movement", "movement.png");
    Category RENDER = new ResourceImageCategory("Render", "render.png");
    Category PLAYER = new ResourceImageCategory("Player", "player.png");
    Category WORLD = new ResourceImageCategory("World", "world.png");
    Category CLIENT = new ResourceImageCategory("Client", "client.png");

    static Category[] values = {COMBAT, MOVEMENT, RENDER, PLAYER, WORLD, CLIENT};

    static Category[] getDefaultCategories() {
        return new Category[]{COMBAT, MOVEMENT, RENDER, PLAYER, WORLD};
    }

    static TextPreview getIconTextPreview(String text, EspColor color) {
        return GuiUtils.prepareText(text, "IconFont", color, 9, true);
    }

    static TextPreview getTextIcons(Category category, EspColor color) {
        String name = category.getName();
        if (name.equals(COMBAT.getName())) {
            return getIconTextPreview("b", color);
        } else if (name.equals(MOVEMENT.getName())) {
            return getIconTextPreview("8", color);
        } else if (name.equals(RENDER.getName())) {
            return getIconTextPreview("a", color);
        } else if (name.equals(PLAYER.getName())) {
            return getIconTextPreview("c", color);
        } else if (name.equals(WORLD.getName())) {
            return getIconTextPreview("3", color);
        } else if (name.equals(CLIENT.getName())) {
            return getIconTextPreview("9", color);
        }
        return null;
    }

    String getName();

    Image getIcon();
}
