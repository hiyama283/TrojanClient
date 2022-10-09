package net.sushiclient.client.utils.elements.sound;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.sushiclient.client.IMC;

public class BreakSound implements IMC {
    public static final ISound sound;

    private static final String song = "break";
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
                return mc.player != null ? (float) mc.player.posY: 0;
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
