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

package net.sushiclient.client.utils.render;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Collection;
import java.util.function.Consumer;

public class SearchUtils {

    public static void find(Chunk chunk, Collection<Block> targets, Consumer<BlockPos> consumer) {
        World world = chunk.getWorld();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos pos = new BlockPos((chunk.x << 4) + x, y, (chunk.z << 4) + z);
                    Block block = chunk.getBlockState(pos).getBlock();
                    if (targets.contains(block)) consumer.accept(pos);
                }
            }
        }
    }
}
