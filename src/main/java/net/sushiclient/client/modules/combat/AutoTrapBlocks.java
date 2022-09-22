package net.sushiclient.client.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public enum AutoTrapBlocks {
    OBSIDIAN(Blocks.OBSIDIAN),
    ANVIL(Blocks.ANVIL),
    ;


    private final Block block;
    AutoTrapBlocks(Block useBlock) {
        block = useBlock;
    }

    public Block getBlock() {
        return block;
    }
}
