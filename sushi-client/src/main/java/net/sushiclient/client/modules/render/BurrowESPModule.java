package net.sushiclient.client.modules.render;

import ibxm.Player;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.tick.ClientTickEvent;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.player.PlayerUtils;
import net.sushiclient.client.utils.player.PositionUtils;
import net.sushiclient.client.utils.render.RenderUtils;
import net.sushiclient.client.utils.world.BlockUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BurrowESPModule extends BaseModule implements ModuleSuffix {
    private final Configuration<EspColor> color;
    private final List<BlockPos> target = new ArrayList<>();
    public BurrowESPModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        color = provider.get("color", "Color", null, EspColor.class,
                new EspColor(new Color(255, 0, 0), false, true));
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        for (BlockPos blockPos : new HashSet<>(target)) {
            GlStateManager.disableDepth();
            RenderUtils.drawFilled(getBox(getWorld(), blockPos), color.getValue().getColor());
            GlStateManager.enableDepth();
        }
    }

    @EventHandler(timing = EventTiming.POST)
    public void onClientTick(ClientTickEvent e) {
        target.clear();
        for (EntityPlayer nearbyPlayer : getWorld().playerEntities) {
            BlockPos blockPos = PositionUtils.toBlockPos(nearbyPlayer.getPositionVector());
            chatLog(blockPos.getX() + ":" + blockPos.getY() + ":" + blockPos.getZ());
            if (PlayerUtils.isPlayerBurrow(nearbyPlayer)) {
                synchronized (target) {
                    target.add(blockPos);
                }
            }
        }
    }

    private AxisAlignedBB getBox(World world, BlockPos origin) {
        return world.getBlockState(origin).getBoundingBox(world, origin).offset(origin);
    }

    @Override
    public String getDefaultName() {
        return "BurrowESP";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.RENDER;
    }

    @Override
    public String getSuffix() {
        return String.valueOf(target.size());
    }
}
