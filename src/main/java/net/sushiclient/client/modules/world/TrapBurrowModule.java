package net.sushiclient.client.modules.world;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.world.BlockPlaceInfo;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Objects;

public class TrapBurrowModule extends BaseModule {
    private final Configuration<DoubleRange> offset;
    private final Configuration<Boolean> packetPlace;
    private final Configuration<EnumHand> placeHand;
    private final Configuration<Boolean> onlyInHole;
    private final Configuration<Boolean> placeAssistBlock;
    private int step;
    public TrapBurrowModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        offset = provider.get("offset", "Offset", null, DoubleRange.class, new DoubleRange(0.2, 0.6, 0.1, 0.1, 1));
        packetPlace = provider.get("packet_place", "Packet place", null, Boolean.class, true);
        placeHand = provider.get("place_hand", "Place hand", null, EnumHand.class, EnumHand.MAIN_HAND);
        onlyInHole = provider.get("only_in_hole", "Only in hole", null, Boolean.class, true);
        placeAssistBlock = provider.get("place_assist_block", "Place assist block", null, Boolean.class, true);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        step = 0;
        ////////////////////////////////
        if (!placeAssistBlock.getValue()) {
            BurrowUtils.burrow(BurrowLogType.ALL, false, onlyInHole.getValue(),
                    packetPlace.getValue(), offset.getValue().getCurrent(), placeHand.getValue());
            setEnabled(false);
        }
    }
    
    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (PlayerUtils.isPlayerBurrow()) {
            chatLog("Successfully placed.");
            setEnabled(false);
            return;
        }

        BlockPos  playerPos = new BlockPos(mc.player);

        BlockPos addFacingPos = playerPos.add(EnumFacing.NORTH.getDirectionVec());
        Block block = BlockUtils.getBlock(addFacingPos.add(0, -1, 0));
        if (block != Blocks.AIR)
            step = 1;

        if (BlockUtils.getBlock(addFacingPos) != Blocks.AIR)
            step = 2;

        ItemSlot obsidianSlot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), InventoryType.values());
        switch (step) {
            case 0:
                if (obsidianSlot == null || Objects.isNull(mc.player) || Objects.isNull(mc.world)) {
                    setEnabled(false, "Cannot find obsidian.");
                    return;
                } else {
                    InventoryUtils.silentSwitch(packetPlace.getValue(), obsidianSlot.getIndex(), () -> {
                        PositionUtils.move(playerPos.getX() + 0.5, playerPos.getY(), playerPos.getZ() + 0.5, 0,
                                0, mc.player.onGround, PositionMask.POSITION);
                        BlockUtils.lowArgPlace(playerPos.add(EnumFacing.NORTH.getDirectionVec()).add(0, -1, 0), packetPlace.getValue(), placeHand.getValue());
                    });
                }
                break;
            case 1:
                if (obsidianSlot == null || Objects.isNull(mc.player) || Objects.isNull(mc.world)) {
                    setEnabled(false, "Cannot find obsidian.");
                    return;
                } else {
                    InventoryUtils.silentSwitch(packetPlace.getValue(), obsidianSlot.getIndex(), () -> {
                        PositionUtils.move(playerPos.getX() + 0.5, playerPos.getY(), playerPos.getZ() + 0.5, 0,
                                0, mc.player.onGround, PositionMask.POSITION);
                        BlockUtils.lowArgPlace(playerPos.add(EnumFacing.NORTH.getDirectionVec()), packetPlace.getValue(), placeHand.getValue());
                    });
                }
                break;
            case 2:
                BurrowUtils.burrow(BurrowLogType.ERROR, false, onlyInHole.getValue(),
                        packetPlace.getValue(), offset.getValue().getCurrent(), placeHand.getValue());
                break;
        }
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "TrapBurrow";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }
}
