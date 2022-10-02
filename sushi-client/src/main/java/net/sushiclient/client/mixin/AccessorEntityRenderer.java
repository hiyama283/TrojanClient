package net.sushiclient.client.mixin;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface AccessorEntityRenderer {

    @Invoker("getFOVModifier")
    float invokeGetFOVModifier(float partialTicks, boolean useFOVSetting);
}
