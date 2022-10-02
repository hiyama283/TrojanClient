package net.sushiclient.client.modules.world;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.modules.movement.PhaseWalkRewriteModule;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Objects;

public class TrapBurrowModule extends BaseModule implements ModuleSuffix {
    private final Configuration<DoubleRange> offset;
    private final Configuration<Boolean> packetPlace;
    private final Configuration<EnumHand> placeHand;
    private final Configuration<Boolean> onlyInHole;
    private final Configuration<Boolean> placeAssistBlock;
    private final Configuration<Boolean> antiGhostBlock;
    private final Configuration<Boolean> onMoveBurrowed;
    private final Configuration<Boolean> burrowOnSneak;
    private final Configuration<IntRange> tryPlaceCount;
    private final Configuration<Boolean> faceObsidian;
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
        tryPlaceCount = provider.get("try_place_count", "Try place count", null, IntRange.class,
                new IntRange(20, 10, 3, 1));
        faceObsidian = provider.get("face_obsidian", "Face obsidian", null, Boolean.class, false);
    }

    private BlockPos searchMovePos(EntityPlayerSP playerSP) {
        BlockPos pos = BlockUtils.toBlockPos(playerSP.getPositionVector());
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

        for (BlockPos offset : offsets) {
            BlockPos addPos = pos.add(offset);
            if (BlockUtils.getBlock(addPos) != Blocks.AIR) {
                return addPos;
            }
        }

        return null;
    }

    private void pausePhaseWalkRewrite(boolean b) {
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!(module instanceof PhaseWalkRewriteModule)) continue;
            module.setPaused(b);
        }
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        step = 0;
        tryCount = 0;
        toggledOn = false;
        sneaked = false;
        ////////////////////////////////
        if (burrowOnSneak.getValue()) {
            return;
        }

        if (PlayerUtils.isPlayerInClip()) {
            if (!burrowOnSneak.getValue()) setEnabled(false);
            return;
        }

        if (onlyInHole.getValue() && (!PositionUtils.isPlayerInHole() && !PlayerUtils.isPlayerBurrow())) {
            setEnabled(false, "You are not in hole!");
            return;
        }

        if (!placeAssistBlock.getValue() || onlyInHole.getValue() || PositionUtils.isPlayerInHole() ||
            !BlockUtils.isAir(getWorld(), getPlayer().getPosition().add(EnumFacing.NORTH.getDirectionVec()))) {

            BurrowUtils.burrow(BurrowLogType.ALL, false, onlyInHole.getValue(),
                    packetPlace.getValue(), offset.getValue().getCurrent(), placeHand.getValue(), faceObsidian.getValue());

            if (onMoveBurrowed.getValue()) {
                burrowOnMove(getPlayer());
            }
            setEnabled(false);
        }
    }

    private void chgTryCount(int i) {
        // chatLog("Before:" + i);
        tryCount = i;
        // chatLog("After:" + i);
    }

    private void burrowOnMove(EntityPlayerSP player) {
        BlockPos movePos = searchMovePos(player);
        if (movePos == null) return;

        PositionUtils.move(movePos.getX(), movePos.getY(), movePos.getZ(), 0, 0, getPlayer().onGround, PositionMask.POSITION);
    }

    private void onBurrowed() {
        chatLog("Successfully placed.");

        if (onMoveBurrowed.getValue()) {
            burrowOnMove(getPlayer());
        }

        if (burrowOnSneak.getValue()) {
            toggledOn = false;
            step = 0;
            chgTryCount(0);
            return;
        }

        setEnabled(false);
    }

    private boolean toggledOn;
    private boolean sneaked;
    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (tryCount >= tryPlaceCount.getValue().getCurrent()) {
            chatLog("Over try count.");

            if (burrowOnSneak.getValue()) {
                toggledOn = false;
                step = 0;
                chgTryCount(0);
            } else {
                setEnabled(false);
            }
            return;
        }

        if (burrowOnSneak.getValue()) {
            if (sneaked) {
                if (!getPlayer().isSneaking()) {
                    sneaked = false;
                } else
                    return;
            }

            if (PlayerUtils.isPlayerBurrow() && toggledOn) {
                onBurrowed();
                return;
            }

            if (!toggledOn && !getPlayer().isSneaking()) return;

            if (onlyInHole.getValue() && (!PositionUtils.isPlayerInHole() && !EntityUtils.isInsideBlock(getPlayer()))) {
                chatLog("You are not in hole!");
                toggledOn = false;
                sneaked = true;
                step = 0;
                chgTryCount(0);
                return;
            }

            if (PlayerUtils.isPlayerInClip()) return;

            if (!toggledOn) {
                toggledOn = true;
                sneaked = true;
                step = 0;
                chgTryCount(0);
            }
        }

        if (PlayerUtils.isPlayerBurrow()) {
            onBurrowed();
            return;
        }

        if (PlayerUtils.isPlayerInClip()) return;

        if (Objects.isNull(InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), InventoryType.values()))) {
            setEnabled(false, "Cannot find trap door.");
            return;
        }

        BlockPos playerPos = new BlockPos(mc.player);

        if (onlyInHole.getValue()) {
            BurrowUtils.burrow(BurrowLogType.ERROR, false, onlyInHole.getValue(),
                    packetPlace.getValue(), offset.getValue().getCurrent(), placeHand.getValue(), faceObsidian.getValue());
            chgTryCount(tryCount + 1);
            return;
        }

        BlockPos downSidePos = playerPos.add(EnumFacing.NORTH.getDirectionVec()).add(0, -1, 0);
        BlockPos sidePos = playerPos.add(EnumFacing.NORTH.getDirectionVec());

        BlockPos addFacingPos = playerPos.add(EnumFacing.NORTH.getDirectionVec());
        Block block = BlockUtils.getBlock(addFacingPos.add(0, -1, 0));
        if (block != Blocks.AIR)
            step = 1;
        if (BlockUtils.getBlock(addFacingPos) != Blocks.AIR)
            step = 2;

        ItemSlot obsidianSlot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), InventoryType.values());
        chgTryCount(tryCount + 1);
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
                }
                break;
            case 2:
                boolean r = BurrowUtils.burrow(BurrowLogType.ERROR, false, onlyInHole.getValue(),
                        packetPlace.getValue(), offset.getValue().getCurrent(), placeHand.getValue(), faceObsidian.getValue());

                if (!r) {
                    toggledOn = false;

                    if (burrowOnSneak.getValue()) return;

                    setEnabled(false);
                } else if (antiGhostBlock.getValue())
                    BlockUtils.checkGhostBlock(playerPos);
                break;
        }
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        pausePhaseWalkRewrite(false);
    }

    @Override
    public String getDefaultName() {
        return "TrapBurrow";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.WORLD;
    }

    @Override
    public String getSuffix() {
        if (!burrowOnSneak.getValue()) return null;

        if (PlayerUtils.isPlayerInClip()) {
            return "Clipping! cant place";
        } else if (toggledOn) {
            return "On step=" + step + " try=" + tryCount;
        } else
            return "No places";
    }
}
