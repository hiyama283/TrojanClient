package net.sushiclient.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;

public interface IMC {
    Minecraft mc = Minecraft.getMinecraft();
    default Minecraft getClient() {
        return Minecraft.getMinecraft();
    }
    default EntityPlayerSP getPlayer() {
        return getClient().player;
    }
    default PlayerControllerMP getController() {
        return getClient().playerController;
    }
    default WorldClient getWorld() {
        return getClient().world;
    }
    default void sendPacket(Packet<?> packet) {
        NetHandlerPlayClient connection = getPlayer().connection;
        if (connection != null) connection.sendPacket(packet);
    }
}
