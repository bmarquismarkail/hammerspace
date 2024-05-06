package net.vakror.hammerspace.server.handlers;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.vakror.hammerspace.capability.TeleporterProvider;
import net.vakror.hammerspace.dimension.HammerspaceTeleporter;
import net.vakror.hammerspace.handlers.ITeleporterHandler;
import net.vakror.hammerspace.item.custom.TeleporterItem;
import net.vakror.hammerspace.network.NetworkHandler;
import net.vakror.hammerspace.network.packets.TeleportRequestPacket;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ServerTeleporterHandler implements ITeleporterHandler {
    @Override
    public void handleTeleport(Player player, TeleporterItem item, InteractionHand hand) {
        player.getItemInHand(hand).getCapability(TeleporterProvider.TELEPORTER).ifPresent((teleporter -> {
            if (!teleporter.dimensionId().equals("")) {
                if (!player.level().dimension().location().equals(teleporter.getDimIdAsResourceLocation())) {
                    NetworkHandler.CHANNEL.sendToServer(new TeleportRequestPacket(teleporter.dimensionId(), player.position().x, (int) player.position().y, (int) player.position().z));
                } else {
                    AtomicReference<ServerLevel> toDimension = new AtomicReference<>(Objects.requireNonNull(player.level().getServer()).overworld());
                    player.level().getServer().getAllLevels().forEach((dim -> {
                        if (dim.dimensionTypeId().location().toString().equals(teleporter.fromDimensionTypeId())) {
                            toDimension.set(dim);
                        }
                    }));
                    if (toDimension.get() != null) {
                        player.changeDimension(toDimension.get(), new HammerspaceTeleporter(player.getItemInHand(hand), (ServerLevel) player.level(), teleporter.lastUsedLocation()));
                    }
                }
            }
        }));
    }
}
