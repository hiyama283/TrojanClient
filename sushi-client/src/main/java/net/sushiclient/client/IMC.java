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

package net.sushiclient.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;

public interface IMC {
    Minecraft mc = Minecraft.getMinecraft();

    default Minecraft getClient() {
        return Minecraft.getMinecraft();
    }

    default EntityPlayerSP getPlayer() {
        return getClient().player;
    }

    default PlayerControllerMP getController() {
        return getClient().playerController;
    }

    default WorldClient getWorld() {
        return getClient().world;
    }

    default void sendPacket(Packet<?> packet) {
        NetHandlerPlayClient connection = getPlayer().connection;
        if (connection != null) connection.sendPacket(packet);
    }
}
