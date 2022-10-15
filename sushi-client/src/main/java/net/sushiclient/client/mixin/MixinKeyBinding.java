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

import net.minecraft.client.settings.KeyBinding;
import net.sushiclient.client.events.EventHandlers;
import net.sushiclient.client.events.input.KeyDownCheckEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
abstract public class MixinKeyBinding {

    @Inject(at = @At("HEAD"), method = "isKeyDown", cancellable = true)
    public void isKeyDown(CallbackInfoReturnable<Boolean> cir) {
        KeyBinding k = (KeyBinding) (Object) this;
        boolean pressed = ((AccessorKeyBinding) this).isPressed0();
        boolean original = pressed && k.getKeyConflictContext().isActive() && k.getKeyModifier().isActive(k.getKeyConflictContext());
        KeyDownCheckEvent event = new KeyDownCheckEvent(k, pressed, original);
        EventHandlers.callEvent(event);
        cir.setReturnValue(event.getResult());
        cir.cancel();
    }
}
