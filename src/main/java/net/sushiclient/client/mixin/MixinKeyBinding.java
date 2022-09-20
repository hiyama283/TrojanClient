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
