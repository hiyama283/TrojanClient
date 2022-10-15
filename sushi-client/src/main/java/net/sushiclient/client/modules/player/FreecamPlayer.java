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

package net.sushiclient.client.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.sushiclient.client.utils.player.MovementUtils;

public class FreecamPlayer extends EntityOtherPlayerMP {

    private final EntityPlayerSP original;

    public FreecamPlayer(World worldIn) {
        super(worldIn, Minecraft.getMinecraft().getSession().getProfile());
        original = Minecraft.getMinecraft().player;
        if (original == null) throw new IllegalStateException();
        copyLocationAndAnglesFrom(original);
        capabilities.allowFlying = true;
        capabilities.isFlying = true;
    }

    private int toInt(KeyBinding plus, KeyBinding minus) {
        int result = 0;
        result += plus.isKeyDown() ? 1 : 0;
        result -= minus.isKeyDown() ? 1 : 0;
        return result;
    }

    @Override
    public void onLivingUpdate() {
        setHealth(original.getHealth());
        setAbsorptionAmount(original.getAbsorptionAmount());
        inventory.copyInventory(original.inventory);
        updateEntityActionState();
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        moveForward = toInt(settings.keyBindForward, settings.keyBindBack);
        moveVertical = toInt(settings.keyBindJump, settings.keyBindSneak);
        moveStrafing = toInt(settings.keyBindLeft, settings.keyBindRight);
        Vec2f motionXZ = MovementUtils.toWorld(new Vec2f(moveForward, moveStrafing), rotationYaw);
        Vec3d motionXYZ = new Vec3d(motionXZ.x, moveVertical, motionXZ.y);
        motionX = motionXYZ.x;
        motionY = motionXYZ.y;
        motionZ = motionXYZ.z;
        setSprinting(settings.keyBindSprint.isKeyDown());
        if (isSprinting()) {
            motionX *= 1.5;
            motionY *= 1.5;
            motionZ *= 1.5;
        }
        move(MoverType.SELF, motionX, motionY, motionZ);
    }

    @Override
    public boolean isSpectator() {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer player) {
        return true;
    }
}
