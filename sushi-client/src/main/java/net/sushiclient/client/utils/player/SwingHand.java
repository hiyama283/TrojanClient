package net.sushiclient.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.sushiclient.client.config.data.Named;

public enum SwingHand implements Named {
    MAIN(EnumHand.MAIN_HAND, "Main"),
    OFFHAND(EnumHand.OFF_HAND, "Offhand"),
    None(null, "None")
    ;

    private final EnumHand hand;
    private final String name;
    SwingHand(EnumHand hand, String name) {
        this.hand = hand;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public EnumHand getHand() {
        return hand;
    }

    public void swing() {
        if (getHand() != null)
            Minecraft.getMinecraft().player.swingArm(getHand());
    }
}
