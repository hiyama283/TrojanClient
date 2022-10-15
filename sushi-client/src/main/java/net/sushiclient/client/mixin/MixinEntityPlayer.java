/*
 * Contact github.com/hiyama283
 * Project "sushi-client"
 *
 * Copyright 2022 hiyama283
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sushiclient.client.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerTickEvent;
import net.sushiclient.client.events.player.PlayerTravelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
abstract public class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Inject(at = @At("HEAD"), method = "onUpdate", cancellable = true)
    public void preOnUpdate(CallbackInfo ci) {
        PlayerTickEvent event = new PlayerTickEvent(EventTiming.PRE);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "onUpdate")
    public void postOnUpdate(CallbackInfo ci) {
        PlayerTickEvent event = new PlayerTickEvent(EventTiming.POST);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "travel", cancellable = true)
    public void preTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerTravelEvent pre = new PlayerTravelEvent(EventTiming.PRE, strafe, vertical, forward);
        EventHandlers.callEvent(pre);
        if (pre.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "travel")
    public void postTravel(float strafe, float vertical, float forward, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        EventHandlers.callEvent(new PlayerTravelEvent(EventTiming.POST, strafe, vertical, forward));
    }
}
