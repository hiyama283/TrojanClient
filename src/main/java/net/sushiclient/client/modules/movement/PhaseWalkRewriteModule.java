package net.sushiclient.client.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.PhaseWalkTimer;
import net.sushiclient.client.utils.TpsUtils;
import net.sushiclient.client.utils.player.MovementUtils;
import net.sushiclient.client.utils.player.PlayerUtils;
import net.sushiclient.client.utils.player.PositionMask;
import net.sushiclient.client.utils.player.PositionUtils;

public class PhaseWalkRewriteModule extends BaseModule implements ModuleSuffix {
    private final Configuration<Boolean> tpsSync;
    private final Configuration<Boolean> capAt20;
    private final Configuration<DoubleRange> multiplier;
    private final Configuration<Boolean> jumpLimit;
    private final Configuration<Boolean> shiftLimit;
    private boolean suffix;
    private boolean sneakedFlag;
    private boolean jumpedFlag;
    private final PhaseWalkTimer timer = new PhaseWalkTimer();
    private boolean firstStart = true;
    private long startTime = 0;
    public PhaseWalkRewriteModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        tpsSync = provider.get("tps_sync", "Tps sync", null, Boolean.class, false);
        capAt20 = provider.get("cap_at_20", "Cap at 20", null, Boolean.class, true,
                tpsSync::getValue, false, 0);
        multiplier = provider.get("multiplier", "Multiplier", null, DoubleRange.class, new DoubleRange(0.5, 5, 0.1, 0.1, 1));
        jumpLimit = provider.get("jump_limit", "Jump limit", null, Boolean.class, true);
        shiftLimit = provider.get("shift_limit", "Shift limit", null, Boolean.class, true);
    }

    private boolean paused = false;
    private void speedModulePauseManager(boolean b) {
        if (paused && b)
            return;

        paused = b;
        for (Module module : Sushi.getProfile().getModules().getAll()) {
            if (!(module instanceof SpeedModule)) continue;
            module.setPaused(b);
        }
    }

    private void checkIsPause() {
        if (paused)
            speedModulePauseManager(false);
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerTravel(PlayerTravelEvent e) {
        if (EntityUtils.isInsideBlock(getPlayer()) && PlayerUtils.isPlayerInClip()) {
            if (firstStart) {
                timer.start();
                startTime = System.currentTimeMillis();
                firstStart = false;
            }
            speedModulePauseManager(true);

            EntityPlayerSP player = getPlayer();
            if (player.isElytraFlying()) return;
            Vec3d inputs = MovementUtils.getMoveInputs(player).normalize();
            // chatLog("Input X:" + inputs.x + " Y:" + inputs.y + " Z:" + inputs.z);
            suffix = true;
            if (shiftLimit.getValue() && sneakedFlag && !getPlayer().isSneaking())
                sneakedFlag = false;
            if (jumpLimit.getValue() && jumpedFlag && inputs.y == 0)
                jumpedFlag = false;


            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;

            player.noClip = EntityUtils.isInsideBlock(getPlayer());
            player.fallDistance = 0;
            player.onGround = false;

            double x = inputs.x;
            double z = inputs.z;

            x *= multiplier.getValue().getCurrent();
            z *= multiplier.getValue().getCurrent();
            // chatLog("Multiply X:" + x + " Z:" + z);

            if (tpsSync.getValue()) {
                double tps = TpsUtils.getTps();
                if (capAt20.getValue()) tps = Math.max(20, tps);
                tps /= 20;

                x *= tps;
                z *= tps;
                // chatLog("Tps sync X:" + x + " Z:" + z);
            }

            Vec2f vec = MovementUtils.toWorld(new Vec2f((float) x, (float) z), player.rotationYaw);
            player.motionX = vec.x;
            player.motionY = 0;
            player.motionZ = vec.y;

            if (player.isSneaking() && !sneakedFlag) {
                PositionUtils.move(player.posX, player.posY - 1, player.posZ, 0, 0, false, PositionMask.POSITION);
                sneakedFlag = true;
            } else if (inputs.y > 0 && !jumpedFlag) {
                PositionUtils.move(player.posX, player.posY + 1, player.posZ, 0, 0, false, PositionMask.POSITION);
                jumpedFlag = true;
            }

        } else {
            suffix = false;
            firstStart = true;
            speedModulePauseManager(false);

            if (startTime != 0) {
                chatDebugLog(String.valueOf(System.currentTimeMillis() - startTime));
                startTime = 0;
            }
        }
    }

    @Override
    public String getSuffix() {
        return suffix ? "Enabled [" + timer.toString() + "]" : "Disabled";
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        checkIsPause();
    }

    @Override
    public String getDefaultName() {
        return "PhaseWalkRewrite";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
