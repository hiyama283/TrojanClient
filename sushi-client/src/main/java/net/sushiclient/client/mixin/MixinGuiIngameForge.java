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

import net.minecraftforge.client.GuiIngameForge;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.EventTiming;
import net.sushiclient.client.events.render.OverlayRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge {

    @Inject(at = @At("HEAD"), method = "renderGameOverlay")
    public void preRenderGameOverlay(float partialTicks, CallbackInfo info) {
        EventHandlers.callEvent(new OverlayRenderEvent(EventTiming.PRE));
    }

    @Inject(at = @At("TAIL"), method = "renderGameOverlay")
    public void postRenderGameOverlay(float partialTicks, CallbackInfo info) {
        EventHandlers.callEvent(new OverlayRenderEvent(EventTiming.POST));
    }
}
