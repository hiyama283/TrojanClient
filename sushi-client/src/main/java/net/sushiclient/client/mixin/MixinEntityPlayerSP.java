package net.sushiclient.client.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        PlayerMoveEvent pre = new PlayerMoveEvent(EventTiming.PRE, type, x, y, z);
        EventHandlers.callEvent(pre);
        boolean changed = pre.getType() != type || pre.getX() != x || pre.getY() != y || pre.getZ() != z;
        if (pre.isCancelled() || changed) {
            ci.cancel();
            if (changed) {
                double d0 = posX;
                double d1 = posZ;
                super.move(pre.getType(), pre.getX(), pre.getY(), pre.getZ());
                ((AccessorEntityPlayerSP) this).invokeUpdateAutoJump((float) (posX - d0), (float) (posZ - d1));
            }
            return;
        }
        PlayerMoveEvent post = new PlayerMoveEvent(EventTiming.POST, type, x, y, z);
        EventHandlers.callEvent(post);
    }

    @Inject(at = @At("HEAD"), method = "onUpdateWalkingPlayer", cancellable = true)
    public void preOnUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerPacketEvent event = new PlayerPacketEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onUpdateWalkingPlayer")
    public void postOnUpdateWalkingPlayer(CallbackInfo ci) {
        PlayerPacketEvent event = new PlayerPacketEvent(EventTiming.POST);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "pushOutOfBlocks", cancellable = true)
    public void pushOutBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        PlayerPushOutOfBlocksEvent e = new PlayerPushOutOfBlocksEvent(x, y, z);
        EventHandlers.callEvent(e);
        if (e.isCancelled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "isCurrentViewEntity", cancellable = true)
    public void isCurrentViewEntity(CallbackInfoReturnable<Boolean> cir) {
        boolean currentViewEntity = Minecraft.getMinecraft().getRenderViewEntity() == this;
        CurrentViewEntityCheckEvent event = new CurrentViewEntityCheckEvent(EventTiming.PRE, currentViewEntity);
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.isCurrentViewEntity());
        cir.cancel();
    }

    @Inject(at = @At("HEAD"), method = "isUser", cancellable = true)
    public void isUser(CallbackInfoReturnable<Boolean> cir) {
        UserCheckEvent event = new UserCheckEvent(EventTiming.PRE, true);
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.isUser());
        cir.cancel();
    }
}
