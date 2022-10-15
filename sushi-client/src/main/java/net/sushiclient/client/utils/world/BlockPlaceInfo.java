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

package net.sushiclient.client.utils.world;

import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class BlockPlaceInfo {
    private static final float EPSILON = 0.00001F;
    private final BlockPos blockPos;
    private final BlockFace blockFace;

    public BlockPlaceInfo(BlockPos blockPos, BlockFace blockFace) {
        this.blockPos = blockPos;
        this.blockFace = blockFace;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockPlaceInfo that = (BlockPlaceInfo) o;

        if (!Objects.equals(blockPos, that.blockPos)) return false;
        return Objects.equals(blockFace, that.blockFace);
    }

    @Override
    public int hashCode() {
        int result = blockPos != null ? blockPos.hashCode() : 0;
        result = 31 * result + (blockFace != null ? blockFace.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BlockPlaceInfo{" +
                "blockPos=" + blockPos +
                ", blockFace=" + blockFace +
                '}';
    }
}
