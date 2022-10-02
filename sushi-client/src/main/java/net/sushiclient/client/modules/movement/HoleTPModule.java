package net.sushiclient.client.modules.movement;

import net.minecraft.util.math.BlockPos;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.player.PositionMask;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.render.hole.HoleInfo;
import net.sushiclient.client.utils.render.hole.HoleUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HoleTPModule extends BaseModule {
    private final Configuration<DoubleRange> range;
    public HoleTPModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        range = provider.get("range", "range", "Can teleport range", DoubleRange.class, new DoubleRange(2.5, 5, 0.1, 0.1, 1));
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);

        double val = range.getValue().getCurrent();
        List<HoleInfo> distinctHoles = new ArrayList<>();

        int counter = 0;
        for (int i = 0; i < 8; i++) {
            int minX, minY, minZ, maxX, maxY, maxZ;
            int index = counter++ % 8;
            minX = counter / 4 % 2 - 1;
            minY = counter / 2 % 2 - 1;
            minZ = counter % 2 - 1;
            maxX = minX + 1;
            maxY = minY + 1;
            maxZ = minZ + 1;

            // search for holes
            BlockPos pos = BlockUtils.toBlockPos(getPlayer().getPositionVector());
            BlockPos from = new BlockPos(pos.getX() + val * minX, pos.getY() + val * minY, pos.getZ() + val * minZ);
            BlockPos to = new BlockPos(pos.getX() + val * maxX, pos.getY() + val * maxY, pos.getZ() + val * maxZ);
            HoleUtils.findHoles(getWorld(), from, to, false, distinctHoles::add);
        }

        Collections.sort(distinctHoles);
        /*
        try {
            for (int i = 0; i < 5; i++) {
                chatLog("Num:" + i + " Distance:" + distinctHoles.get(i).distance());
            }
        } catch (IndexOutOfBoundsException ignored) {}

         */
        try {
            HoleInfo mostNear = distinctHoles.get(0);
            double mostNearDistance = mostNear.distance();
            BlockPos mostNearPos = mostNear.getBlockPos()[0];
            if (mostNearDistance <= range.getValue().getCurrent()) {
                PositionUtils.move(mostNearPos.getX(), mostNearPos.getY(), mostNearPos.getZ(), 0, 0, mc.player.onGround, PositionMask.POSITION);
                chatLog("Success fully teleported.");
                chatLog("debug distance:" + mostNear.distance() + " X:" + mostNearPos.getX() + " Y:" + mostNearPos.getY() + " Z:" + mostNearPos.getZ()  );
            } else
                chatLog("Hole not found.");
        } catch (IndexOutOfBoundsException ignored) {
            chatLog("Hole not found.");
        }

        setEnabled(false);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
    }

    @Override
    public String getDefaultName() {
        return "HoleTP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
