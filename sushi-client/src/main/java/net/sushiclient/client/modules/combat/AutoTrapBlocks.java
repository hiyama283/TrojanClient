package net.sushiclient.client.modules.combat;

import com.google.gson.annotations.SerializedName;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.sushiclient.client.config.data.Named;

public enum AutoTrapBlocks implements Named {
    @SerializedName("Obsidian")
    OBSIDIAN(Blocks.OBSIDIAN, "Obsidian"),
    @SerializedName("Anvil")
    ANVIL(Blocks.ANVIL, "Anvil"),
    @SerializedName("Web")
    WEB(Blocks.WEB, "Web"),
    ;


    private final Block block;
    private final String name;
    AutoTrapBlocks(Block useBlock, String name) {
        block = useBlock;
        this.name = name;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public String getName() {
        return name;
    }
}
