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
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Objects;

public class TrapBurrowModule extends BaseModule {
    private final Configuration<DoubleRange> offset;
    private final Configuration<Boolean> packetPlace;
    private final Configuration<EnumHand> placeHand;
    private final Configuration<Boolean> onlyInHole;
    private final Configuration<Boolean> placeAssistBlock;
    private final Configuration<Boolean> antiGhostBlock;
    private final Configuration<Boolean> onMoveBurrowed;
    private final Configuration<Boolean> burrowOnSneak;
    private final Configuration<IntRange> tryPlaceCount;
    private int tryCount;
    private int step;
    public TrapBurrowModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        offset = provider.get("offset", "Offset", null, DoubleRange.class, new DoubleRange(0.2, 0.6, 0.1, 0.1, 1));
        packetPlace = provider.get("packet_place", "Packet place", null, Boolean.class, true);
        placeHand = provider.get("place_hand", "Place hand", null, EnumHand.class, EnumHand.MAIN_HAND);
        onlyInHole = provider.get("only_in_hole", "Only in hole", null, Boolean.class, true);
        placeAssistBlock = provider.get("place_assist_block", "Place assist block", null, Boolean.class, true);
        antiGhostBlock = provider.get("anti_ghost_block", "Anti ghost block", null, Boolean.class, true);
        onMoveBurrowed = provider.get("on_burrow_move", "On burrow move", "What is this", Boolean.class, false);
        burrowOnSneak = provider.get("burrow_on_sneak", "Burrow on sneak", "Best config", Boolean.class, false);
        tryPlaceCount = provider.get("try_place_count", "Try place count", null, IntRange.class, new IntRange(20, 10, 3, 1));
    }

    private BlockPos searchMovePos() {
        BlockPos pos = getPlayer().getPosition();
        BlockPos[] offsets = {
                new BlockPos(1, 0, 1),
                new BlockPos(1, 0, 0),
                new BlockPos(1, 0, -1),
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, -1),
                new BlockPos(-1, 0, 1),
                new BlockPos(-1, 0, 0),
                new BlockPos(-1, 0, -1),
                new BlockPos(0, -1, 0),
        };

        BlockPos tpPos = null;
        for (BlockPos offset : offsets) {
            BlockPos addPos = pos.add(offset);
            if (BlockUtils.getBlock(addPos) != Blocks.AIR) {
                tpPos = addPos;
            }
        }

        if (Objects.isNull(tpPos)) return pos;
        return tpPos;
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        step = 0;
        tryCount = 0;
        ////////////////////////////////
        if (onlyInHole.getValue() && !PositionUtils.isPlayerInHole()) {
            setEnabled(false, "You are not in hole!");
            return;
        }

        if (burrowOnSneak.getValue()) {
            return;
        }

        if (!placeAssistBlock.getValue() || onlyInHole.getValue() || PositionUtils.isPlayerInHole() ||
            !BlockUtils.isAir(getWorld(), getPlayer().getPosition().add(EnumFacing.NORTH.getDirectionVec()))) {

            BurrowUtils.burrow(BurrowLogType.ALL, false, onlyInHole.getValue(),
                    packetPlace.getValue(), offset.getValue().getCurrent(), placeHand.getValue());

            if (onMoveBurrowed.getValue()) {
                BlockPos movePos = searchMovePos();
                PositionUtils.move(movePos.getX(), movePos.getY(), movePos.getZ(), 0, 0, getPlayer().onGround, PositionMask.POSITION);
            }
            setEnabled(false);
        }
    }

    private boolean toggledOn;
    private boolean sneaked;
    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (tryCount >= tryPlaceCount.getValue().getCurrent()) {
            if (burrowOnSneak.getValue()) {
                toggledOn = false;
            } else {
                setEnabled(false);
            }
            return;
        }

        if (burrowOnSneak.getValue()) {
            if (sneaked) {
                if (!getPlayer().isSneaking()) sneaked = false;
                return;
            }
            if (!toggledOn && !getPlayer().isSneaking()) return;
            toggledOn = true;
            sneaked = true;
        }

        if (PlayerUtils.isPlayerBurrow()) {
            chatLog("Successfully placed.");

            if (onMoveBurrowed.getValue()) {
                BlockPos movePos = searchMovePos();
                PositionUtils.move(movePos.getX(), movePos.getY(), movePos.getZ(), 0, 0, getPlayer().onGround, PositionMask.POSITION);
            }

            if (burrowOnSneak.getValue()) {
                toggledOn = false;
                return;
            }

            setEnabled(false);
            return;
        }

        if (Objects.isNull(InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), InventoryType.values()))) {
            setEnabled(false, "Cannot find trap door.");
            return;
        }

        BlockPos playerPos = new BlockPos(mc.player);
        BlockPos downSidePos = playerPos.add(EnumFacing.NORTH.getDirectionVec()).add(0, -1, 0);
        BlockPos sidePos = playerPos.add(EnumFacing.NORTH.getDirectionVec());

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

                        BlockUtils.lowArgPlace(downSidePos, packetPlace.getValue(), placeHand.getValue());
                        if (antiGhostBlock.getValue()) BlockUtils.checkGhostBlock(downSidePos);
                    });
                    tryCount++;
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
                        BlockUtils.lowArgPlace(sidePos, packetPlace.getValue(), placeHand.getValue());
                        if (antiGhostBlock.getValue()) BlockUtils.checkGhostBlock(sidePos);
                    });
                    tryCount++;
                }
                break;
            case 2:
                boolean r = BurrowUtils.burrow(BurrowLogType.ERROR, false, onlyInHole.getValue(),
                        packetPlace.getValue(), offset.getValue().getCurrent(), placeHand.getValue());

                tryCount++;
                if (!r) {
                    if (burrowOnSneak.getValue()) {
                        toggledOn = false;
                    }

                    setEnabled(false);
                } else if (antiGhostBlock.getValue())
                    BlockUtils.checkGhostBlock(playerPos);
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
