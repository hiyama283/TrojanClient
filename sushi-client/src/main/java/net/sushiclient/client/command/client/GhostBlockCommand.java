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

package net.sushiclient.client.command.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.command.LogLevel;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;

@CommandAlias(value = "setblock", description = "Sets a ghost block to a specific location")
public class GhostBlockCommand {

    @Default
    public void onDefault(Logger out, Integer x, Integer y, Integer z, Integer id) {
        Block block = Block.getBlockById(id);
        if (block == null) {
            out.send(LogLevel.ERROR, "Could not find Block with block id " + id);
            return;
        }
        WorldClient world = Minecraft.getMinecraft().world;
        if (world == null) {
            out.send(LogLevel.ERROR, "World is not loaded");
            return;
        }
        world.setBlockState(new BlockPos(x, y, z), block.getDefaultState());
    }
}
