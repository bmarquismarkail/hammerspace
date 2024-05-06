package net.vakror.hammerspace.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.vakror.hammerspace.network.packets.TeleportRequestPacket;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("your_mod_id", "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int index = 0;
        CHANNEL.messageBuilder(TeleportRequestPacket.class, index++)
                .encoder(TeleportRequestPacket::encode)
                .decoder(TeleportRequestPacket::decode)
                .consumerMainThread(TeleportRequestPacket::handle)
                .add();
    }
}
