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

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.EntityRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public class MixinRenderManager {

    @Inject(at = @At("HEAD"), method = "renderEntity", cancellable = true)
    public void preRenderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean debug, CallbackInfo ci) {
        if (entityIn == null) return;
        EntityRenderEvent event = new EntityRenderEvent(EventTiming.PRE, !(entityIn instanceof EntityLivingBase),
                entityIn, x, y, z, yaw, partialTicks, debug);

        EventHandlers.callEvent(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "renderEntity")
    public void postRenderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean debug, CallbackInfo ci) {
        if (entityIn == null) return;
        EntityRenderEvent event = new EntityRenderEvent(EventTiming.POST, !(entityIn instanceof EntityLivingBase),
                entityIn, x, y, z, yaw, partialTicks, debug);
        EventHandlers.callEvent(event);
    }
}
