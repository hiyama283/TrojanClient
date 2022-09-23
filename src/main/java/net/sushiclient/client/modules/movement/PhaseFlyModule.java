package net.sushiclient.client.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.sushiclient.client.Sushi;
import net.sushiclient.client.config.Configuration;
import net.sushiclient.client.config.RootConfigurations;
import net.sushiclient.client.config.data.DoubleRange;
import net.sushiclient.client.events.EventHandler;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerPacketEvent;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import net.sushiclient.client.modules.*;
import net.sushiclient.client.utils.EntityUtils;
import net.sushiclient.client.utils.TpsUtils;
import net.sushiclient.client.utils.player.*;

public class PhaseFlyModule extends BaseModule {

    private final Configuration<DoubleRange> horizontal;
    private final Configuration<DoubleRange> vertical;
    private final Configuration<DoubleRange> elytraHorizontal;
    private final Configuration<DoubleRange> elytraVertical;
    private final Configuration<Boolean> auto;
    private final Configuration<Boolean> tpsSync;
    private final Configuration<Boolean> capAt20;
    private final Configuration<Boolean> onStartBurrow;
    private final Configuration<Boolean> packetPlace;
    private final Configuration<Boolean> onlyInHole;
    private final Configuration<EnumHand> placeHand;
    private int stage;

    public PhaseFlyModule(String id, Modules modules, Categories categories, RootConfigurations provider, ModuleFactory factory) {
        super(id, modules, categories, provider, factory);
        horizontal = provider.get("horizontal_speed", "Horizontal Speed", null, DoubleRange.class, new DoubleRange(1, 1, 0, 0.05, 2));
        vertical = provider.get("vertical_speed", "Vertical Speed", null, DoubleRange.class, new DoubleRange(1, 1, 0, 0.05, 2));
        elytraHorizontal = provider.get("elytra_horizontal_speed", "Horizontal Speed(Elytra)", null, DoubleRange.class, new DoubleRange(1, 1, 0, 0.05, 2));
        elytraVertical = provider.get("elytra_vertical_speed", "Vertical Speed(Elytra)", null, DoubleRange.class, new DoubleRange(1, 1, 0, 0.05, 2));
        auto = provider.get("auto", "Auto Phase", null, Boolean.class, true);
        tpsSync = provider.get("tps_sync", "TPS Sync", null, Boolean.class, false);
        capAt20 = provider.get("cap_at_20", "Cap At 20", null, Boolean.class, false, tpsSync::getValue, false, 0);
        onStartBurrow = provider.get("on_start_burrow", "On started burrow", null, Boolean.class, true);
        packetPlace = provider.get("packet_place", "Packet place", null, Boolean.class, true, onStartBurrow::getValue,
                false, 0);
        onlyInHole = provider.get("only_in_hole", "Only in hole", null, Boolean.class, true, onStartBurrow::getValue,
                false, 0);
        placeHand = provider.get("place_hand", "Place hand", null, EnumHand.class, EnumHand.MAIN_HAND, onStartBurrow::getValue,
                false, 0);
    }

    @Override
    public void onEnable() {
        EventHandlers.register(this);
        if (onStartBurrow.getValue()) {
            BurrowUtils.burrow(BurrowLogType.ALL, false, onlyInHole.getValue(), packetPlace.getValue(),
                    0.2, placeHand.getValue());
        }

        for (Module m : Sushi.getProfile().getModules().getAll()) {
            if (!(m instanceof PhaseWalkRewriteModule)) return;
            m.setPaused(true);
        }
    }

    @Override
    public void onDisable() {
        EventHandlers.unregister(this);
        for (Module m : Sushi.getProfile().getModules().getAll()) {
            if (!(m instanceof PhaseWalkRewriteModule)) return;
            m.setPaused(false);
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerTravel(PlayerTravelEvent e) {
        EntityPlayerSP player = getPlayer();
        player.motionX = 0;
        player.motionY = 0;
        player.motionZ = 0;
        if (stage != 0) return;
        player.noClip = EntityUtils.isInsideBlock(getPlayer());
        player.fallDistance = 0;
        player.onGround = false;

        double horizontalSpeed;
        double verticalSpeed;
        if (player.isElytraFlying()) {
            horizontalSpeed = elytraHorizontal.getValue().getCurrent();
            verticalSpeed = elytraVertical.getValue().getCurrent();
        } else {
            horizontalSpeed = horizontal.getValue().getCurrent();
            verticalSpeed = vertical.getValue().getCurrent();
        }
        horizontalSpeed *= 5;
        verticalSpeed *= 5;
        Vec3d inputs = MovementUtils.getMoveInputs(player).normalize();
        float moveForward = (float) (inputs.x * horizontalSpeed);
        float moveUpward = (float) (inputs.y * verticalSpeed);
        float moveStrafe = (float) (inputs.z * horizontalSpeed);


        if (tpsSync.getValue()) {
            double tps = TpsUtils.getTps();
            if (capAt20.getValue()) tps = Math.max(20, tps);
            moveForward *= tps / 20;
            moveStrafe *= tps / 20;
            moveUpward *= tps / 20;
        }

        Vec2f vec = MovementUtils.toWorld(new Vec2f(moveForward, moveStrafe), player.rotationYaw);
        player.motionX = vec.x;
        player.motionY = moveUpward;
        player.motionZ = vec.y;
        // Anti Glide
        if (player.isElytraFlying()) {
            float f = player.rotationPitch * 0.017453292F;
            double d1 = player.getLookVec().length();
            float f4 = MathHelper.cos(f);
            f4 = (float) ((double) f4 * (double) f4 * Math.min(1.0D, d1 / 0.4D));
            player.motionY -= -0.08D + (double) f4 * 0.06D;
        }
    }

    @EventHandler(timing = EventTiming.PRE)
    public void onPlayerPacket(PlayerPacketEvent e) {
        if (!auto.getValue()) return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (stage == 0 &&
                (getPlayer().movementInput.sneak || EntityUtils.isInsideBlock(getPlayer()) ||
                        !EntityUtils.isInsideBlock(getPlayer()) && !EntityUtils.isHittingRoof(getPlayer()))) return;
        if (stage == 0 || stage == 1) {
            player.movementInput.sneak = true;
            stage++;
        } else if (stage == 2) {
            player.movementInput.sneak = true;
            PositionUtils.move(player.posX, player.posY + 0.1, player.posZ, 0, 0, false, PositionMask.POSITION);
            stage++;
        } else if (stage == 3) {
            stage = 0;
        }
    }

    @Override
    public String getDefaultName() {
        return "PhaseFly";
    }

    @Override
    public Category getDefaultCategory() {
        return Category.MOVEMENT;
    }
}
