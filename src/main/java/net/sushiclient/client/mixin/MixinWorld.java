package net.sushiclient.client.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.client.LightUpdateEvent;
import net.sushiclient.client.events.world.RainStrengthGetEvent;
import net.sushiclient.client.events.world.ThunderStrengthGetEvent;
import net.sushiclient.client.events.world.WorldTimeGetEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {

    @Inject(at = @At("HEAD"), method = "getWorldTime", cancellable = true)
    public void getWorldTime(CallbackInfoReturnable<Long> cir) {
        WorldTimeGetEvent event = new WorldTimeGetEvent(EventTiming.PRE, ((World) (Object) this).provider.getWorldTime());
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.getWorldTime());
        cir.cancel();
    }

    @Inject(at = @At("HEAD"), method = "checkLightFor", cancellable = true)
    public void preCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        LightUpdateEvent event = new LightUpdateEvent(EventTiming.PRE, lightType, pos);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(at = @At("TAIL"), method = "checkLightFor")
    public void postCheckLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        LightUpdateEvent event = new LightUpdateEvent(EventTiming.POST, lightType, pos);
        EventHandlers.callEvent(event);
    }

    @Inject(method = "getThunderStrength", at = @At("RETURN"), cancellable = true)
    private void getThunderStrength(float delta, CallbackInfoReturnable<Float> cir) {
        ThunderStrengthGetEvent event = new ThunderStrengthGetEvent(EventTiming.PRE, delta, cir.getReturnValueF());
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.getValue());
    }

    @Inject(method = "getRainStrength", at = @At("RETURN"), cancellable = true)
    private void getRainStrength(float delta, CallbackInfoReturnable<Float> cir) {
        RainStrengthGetEvent event = new RainStrengthGetEvent(EventTiming.PRE, delta, cir.getReturnValueF());
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.getValue());
    }
}
