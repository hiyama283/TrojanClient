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

package net.sushiclient.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public enum EntityType {
    PASSIVE, NEUTRAL, HOSTILE;

    public static boolean match(Entity entity, boolean player, boolean self, boolean mob, boolean passive, boolean neutral, boolean hostile) {
        if (!(entity instanceof EntityLivingBase)) return false;
        EntityPlayerSP entityPlayer = Minecraft.getMinecraft().player;
        if (entity instanceof EntityPlayer) {
            if (entity.getName().equals(entityPlayer == null ? "" : entityPlayer.getName())) return self;
            else return player;
        }
        if (!mob) return false;
        EntityType state = EntityUtils.getEntityType((EntityLivingBase) entity);
        switch (state) {
            case PASSIVE:
                return passive;
            case NEUTRAL:
                return neutral;
            case HOSTILE:
                return hostile;
            default:
                throw new UnsupportedOperationException();
        }
    }

}
