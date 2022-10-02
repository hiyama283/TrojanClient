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
