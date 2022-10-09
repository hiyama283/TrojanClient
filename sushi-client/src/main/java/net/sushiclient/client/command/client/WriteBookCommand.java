package net.sushiclient.client.command.client;

import net.minecraft.network.play.client.CPacketUpdateSign;
import net.sushiclient.client.command.Logger;
import net.sushiclient.client.command.annotation.CommandAlias;
import net.sushiclient.client.command.annotation.Default;

@CommandAlias(value = "writebook", description = "Write random string to book")
public class WriteBookCommand {
    @Default
    public void onDefault(Logger out) {

    }
}
