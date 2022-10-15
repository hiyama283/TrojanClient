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

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.io.Closeable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SearchMap extends ChunkProcessor implements Closeable {

    private final Collection<Block> targets;
    private final HashSet<BlockPos> result = new HashSet<>();

    public SearchMap(Collection<Block> targets) {
        this.targets = targets;
        recalculateAll();
    }

    public Set<BlockPos> getResult() {
        return ImmutableSet.copyOf(result);
    }

    @Override
    protected void reset() {
        result.clear();
    }

    @Override
    protected boolean reduce(Chunk chunk) {
        if (chunk.isLoaded()) SearchUtils.find(chunk, targets, result::add);
        else SearchUtils.find(chunk, targets, result::remove);
        return true;
    }

    @Override
    protected void onBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        result.remove(pos);
        if (targets.contains(newState.getBlock())) result.add(pos);
    }
}
