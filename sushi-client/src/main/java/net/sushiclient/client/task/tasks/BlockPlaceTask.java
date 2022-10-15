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

package net.sushiclient.client.task.tasks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.sushiclient.client.task.TaskAdapter;
import net.sushiclient.client.utils.player.RotateMode;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;
import net.sushiclient.client.utils.world.PlaceOptions;

import java.util.List;

public class BlockPlaceTask extends TaskAdapter<List<BlockPlaceInfo>, Object> {

    private int index = -1;

    private final RotateMode rotateMode;
    private final boolean desync;
    private final boolean swing;
    private final boolean packet;
    private final PlaceOptions[] option;
    private final WorldClient world;

    public BlockPlaceTask(RotateMode rotateMode, boolean desync, boolean packet, boolean swing, PlaceOptions... option) {
        this.rotateMode = rotateMode;
        this.desync = desync;
        this.packet = packet;
        this.swing = swing;
        this.option = option;
        Minecraft minecraft = Minecraft.getMinecraft();
        world = minecraft.world;
    }

    @Override
    public void tick() throws Exception {
        if (getInput() == null) {
            stop(null);
            return;
        }
        BlockPlaceInfo info;
        do {
            if (++index >= getInput().size()) {
                stop(null);
                return;
            }
            info = getInput().get(index);
        } while (!BlockUtils.canPlace(world, info, option));

        BlockPlaceInfo fi = info;
        rotateMode.rotate(info, desync, () -> {
            NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
            BlockUtils.place(fi, packet);
            if (swing && connection != null) {
                connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
        }, null);
    }
}
