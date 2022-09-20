package net.sushiclient.client.modules.world;

import net.minecraft.block.BlockAir;
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
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.*;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.Objects;

public class TrapBurrowModule extends BaseModule {
    private final Configuration<DoubleRange> offset;
    private final Configuration<Boolean> packetPlace;
    private final Configuration<EnumHand> placeHand;
    private final Configuration<Boolean> onlyInHole;
    private final Configuration<Boolean> placeAssistBlock;
    private final Configuration<Boolean> burrowOnEnable;
    private final Configuration<Boolean> noBurrowOnShift;
    private final Configuration<Boolean> alwaysMode;
    private final Configuration<DoubleRange> trapDistance;
    private final Configuration<Boolean> onMoveDisable;
    private final Configuration<Boolean> onSurround;
    private BlockPos onEnablePos;
    public TrapBurrowModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        offset = provider.get("offset", "Offset", null, DoubleRange.class, new DoubleRange(0.2, 0.6, 0.1, 0.1, 1));
        packetPlace = provider.get("packet_place", "Packet place", null, Boolean.class, true);
        placeHand = provider.get("place_hand", "Place hand", null, EnumHand.class, EnumHand.MAIN_HAND);
        onlyInHole = provider.get("only_in_hole", "Only in hole", null, Boolean.class, true);
        placeAssistBlock = provider.get("place_assist_block", "Place assist block", null, Boolean.class, true);
        burrowOnEnable = provider.get("burrow_on_enable", "Burrow on enabled", null, Boolean.class, true);
        noBurrowOnShift = provider.get("no_burrow_on_shift", "No burrow on shift", null, Boolean.class, false);
        alwaysMode = provider.get("always_mode", "Always mode", null, Boolean.class, false);
        trapDistance = provider.get("trap_distance", "Trap distance", null, DoubleRange.class, 
                new DoubleRange(2.0, 5.0, 0.1, 0.1, 1), alwaysMode::isValid, false, 0);
        onMoveDisable = provider.get("on_move_disable", "On move disable", null, Boolean.class,
                true, alwaysMode::isValid, false, 0);
        onSurround = provider.get("on_surround", "On surround", null, Boolean.class,
                false, alwaysMode::isValid, false, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        onEnablePos = mc.player.getPosition();
        ////////////////////////////////
        if (placeAssistBlock.getValue())
            surround();

        if (burrowOnEnable.getValue())
            BurrowUtils.burrow(BurrowLogType.ALL, noBurrowOnShift.getValue(), onlyInHole.getValue(), packetPlace.getValue(),
                    offset.getValue().getCurrent(), placeHand.getValue());

        if (!alwaysMode.getValue())
            setEnabled(false);
    }
    
    @EventHandler(timing = EventTiming.PRE)
    public void onClientTick(ClientTickEvent e) {
        if (onMoveDisable.getValue() && !getPlayer().getPosition().equals(onEnablePos))
            setEnabled(false);
        
        if (onSurround.getValue())
            surround();

        if (alwaysMode.getValue() && EntityUtils.getNearbyPlayers(
                trapDistance.getValue().getCurrent()).size() > 0)
            BurrowUtils.burrow(BurrowLogType.ALL, noBurrowOnShift.getValue(), onlyInHole.getValue(), packetPlace.getValue(),
                    offset.getValue().getCurrent(), placeHand.getValue());
    }

    private void surround() {
        ItemSlot obsidianSlot = InventoryUtils.findItemSlot(Item.getItemFromBlock(Blocks.OBSIDIAN), InventoryType.values());
        if (obsidianSlot == null || Objects.isNull(mc.player) || Objects.isNull(mc.world)) {
            setEnabled(false, "Cannot find obsidian.");
        } else {
            InventoryUtils.silentSwitch(packetPlace.getValue(), obsidianSlot.getIndex(), () -> {
                BlockPos pos = new BlockPos(mc.player);
                PositionUtils.move(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0,
                            0, mc.player.onGround, PositionMask.POSITION);

                BlockUtils.lowArgPlace(pos.add(EnumFacing.NORTH.getDirectionVec()).add(0, -1, 0), packetPlace.getValue());
                BlockUtils.lowArgPlace(pos.add(EnumFacing.NORTH.getDirectionVec()), packetPlace.getValue());
            });
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
