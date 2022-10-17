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

package net.sushiclient.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.sushiclient.client.config.data.Named;

public enum SwingHand implements Named {
    MAIN(EnumHand.MAIN_HAND, "Main"),
    OFFHAND(EnumHand.OFF_HAND, "Offhand"),
    None(null, "None");

    private final EnumHand hand;
    private final String name;

    SwingHand(EnumHand hand, String name) {
        this.hand = hand;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public EnumHand getHand() {
        return hand;
    }

    public void swing() {
        if (getHand() != null)
            Minecraft.getMinecraft().player.swingArm(getHand());
    }
}
