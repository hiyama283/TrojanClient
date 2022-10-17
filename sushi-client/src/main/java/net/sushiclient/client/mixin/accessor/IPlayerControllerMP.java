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

package net.sushiclient.client.mixin.accessor;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerControllerMP.class)
public interface IPlayerControllerMP {

    @Accessor("curBlockDamageMP")
    void setCurrentBlockDamage(float currentBlockDamage);

    @Accessor("curBlockDamageMP")
    float getCurrentBlockDamage();

    @Accessor("blockHitDelay")
    void setBlockHitDelay(int blockHitDelay);

    @Accessor("currentPlayerItem")
    int getCurrentPlayerItem();

    @Accessor("currentPlayerItem")
    void setCurrentPlayerItem(int currentPlayerItem);

    @Invoker("syncCurrentPlayItem")
    void hookSyncCurrentPlayItem();
}