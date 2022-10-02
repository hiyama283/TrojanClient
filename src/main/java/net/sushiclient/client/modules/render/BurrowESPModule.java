package net.sushiclient.client.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.EspColor;
import net.sushiclient.client.config.data.IntRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.world.WorldRenderEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BurrowESPModule extends BaseModule implements ModuleSuffix {
    private final Configuration<IntRange> range;
    private final Configuration<EspColor> color;
    private final List<BlockPos> target = new ArrayList<>();
    public BurrowESPModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        range = provider.get("range", "Range", null, IntRange.class, new IntRange(10, 20, 1, 1));
        color = provider.get("color", "Color", null, EspColor.class,
                new EspColor(new Color(255, 0, 0), false, true));
    }

    @EventHandler(timing = EventTiming.POST)
    public void onWorldRender(WorldRenderEvent e) {
        for (BlockPos blockPos : target) {
            GlStateManager.disableDepth();
            RenderUtils.drawFilled(getBox(getWorld(), blockPos), color.getValue().getColor());
            GlStateManager.enableDepth();
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
