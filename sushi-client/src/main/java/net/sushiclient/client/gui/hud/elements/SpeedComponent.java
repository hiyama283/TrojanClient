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
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.Configurations;
import net.sushiclient.client.gui.hud.TextElementComponent;
import net.sushiclient.client.utils.player.SpeedUtils;

import java.text.DecimalFormat;

public class SpeedComponent extends TextElementComponent {

    private static final DecimalFormat FORMATTER = new DecimalFormat("0.0");
    private final Configuration<String> format;

    public SpeedComponent(Configurations configurations, String id, String name) {
        super(configurations, id, name);
        format = getConfiguration("format", "Format", null, String.class, "{m/s} m/s");
    }

    @Override
    protected String getText() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return "";
        return format.getValue().replace("{m/s}", FORMATTER.format(SpeedUtils.getMps(player)))
                .replace("{km/h}", FORMATTER.format(SpeedUtils.getKmph(player)));
    }
}
