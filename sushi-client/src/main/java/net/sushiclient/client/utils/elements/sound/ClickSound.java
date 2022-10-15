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

package net.sushiclient.client.utils.elements.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.sushiclient.client.IMC;

public class ClickSound implements IMC {
    public static final ISound sound;

    private static final String song = "click";
    private static final ResourceLocation loc = new ResourceLocation("sounds/" + song + ".ogg");

    static {
        sound = new ISound() {

            private final int pitch = 1;
            private final int volume = 1000000;

            @Override
            public ResourceLocation getSoundLocation() {
                return loc;
            }

            @Override
            public SoundEventAccessor createAccessor(SoundHandler soundHandler) {
                return new SoundEventAccessor(loc, "Pitbull");
            }

            @Override
            public Sound getSound() {
                return new Sound(song, volume, pitch, 1, Sound.Type.SOUND_EVENT, false);
            }

            @Override
            public SoundCategory getCategory() {
                return SoundCategory.VOICE;
            }

            @Override
            public boolean canRepeat() {
                return false;
            }

            @Override
            public int getRepeatDelay() {
                return 2;
            }

            @Override
            public float getVolume() {
                return volume;
            }

            @Override
            public float getPitch() {
                return pitch;
            }

            @Override
            public float getXPosF() {
                return mc.player != null ? (float) mc.player.posX : 0;
            }

            @Override
            public float getYPosF() {
                return mc.player != null ? (float) mc.player.posY : 0;
            }

            @Override
            public float getZPosF() {
                return mc.player != null ? (float) mc.player.posZ : 0;
            }

            @Override
            public AttenuationType getAttenuationType() {
                return AttenuationType.LINEAR;
            }
        };
    }

}
