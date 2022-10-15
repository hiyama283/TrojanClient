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

package net.sushiclient.client.modules.movement;

import net.minecraft.client.Minecraft;

public interface Step {
    boolean step(double dX, double dY, double dZ, double toY, boolean phase);

    boolean reverse(double dX, double dY, double dZ, double toY, boolean phase);

    default boolean step(double dX, double dY, double dZ, boolean phase) {
        return step(dX, dY, dZ, Minecraft.getMinecraft().player.posY + dY, phase);
    }

    default boolean reverse(double dX, double dY, double dZ, boolean phase) {
        return reverse(dX, dY, dZ, Minecraft.getMinecraft().player.posY + dY, phase);
    }
}
