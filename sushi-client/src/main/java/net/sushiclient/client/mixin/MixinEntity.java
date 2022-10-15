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
import net.minecraft.entity.Entity;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.player.PlayerPushEvent;
import net.sushiclient.client.events.player.PlayerTurnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(at = @At("HEAD"), method = "applyEntityCollision", cancellable = true)
    public void preApplyEntityCollision(Entity entityIn, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerPushEvent event = new PlayerPushEvent(EventTiming.PRE, entityIn);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }


    @Inject(at = @At("TAIL"), method = "applyEntityCollision")
    public void postApplyEntityCollision(Entity entityIn, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerPushEvent event = new PlayerPushEvent(EventTiming.POST, entityIn);
        EventHandlers.callEvent(event);
    }

    @Inject(at = @At("HEAD"), method = "turn", cancellable = true)
    public void preTurn(float yaw, float pitch, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerTurnEvent event = new PlayerTurnEvent(EventTiming.PRE, yaw, pitch);
        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "turn")
    public void postTurn(float yaw, float pitch, CallbackInfo ci) {
        if (!((Object) this instanceof EntityPlayerSP)) return;
        PlayerTurnEvent event = new PlayerTurnEvent(EventTiming.POST, yaw, pitch);
        EventHandlers.callEvent(event);
    }
}
