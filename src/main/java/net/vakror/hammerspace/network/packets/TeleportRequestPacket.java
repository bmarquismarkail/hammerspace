package net.vakror.hammerspace.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import net.vakror.hammerspace.capability.HammerspaceProvider;
import net.vakror.hammerspace.capability.TeleporterProvider;
import net.vakror.hammerspace.dimension.DimensionUtils;
import net.vakror.hammerspace.dimension.HammerspaceTeleporter;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class TeleportRequestPacket {
    private final String dimensionId;
    private final double posX, posY, posZ;

    public TeleportRequestPacket(String dimensionId, double posX, double posY, double posZ) {
        this.dimensionId = dimensionId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public static void encode(TeleportRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.dimensionId);
        buffer.writeDouble(packet.posX);
        buffer.writeDouble(packet.posY);
        buffer.writeDouble(packet.posZ);
    }

    public static TeleportRequestPacket decode(FriendlyByteBuf buffer) {
        return new TeleportRequestPacket(
                buffer.readUtf(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
    }

    // Server-side packet handler logic
    public static void handle(TeleportRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Ensure we are on the server side and have a valid player
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                player.getItemInHand(InteractionHand.MAIN_HAND).getCapability(TeleporterProvider.TELEPORTER).ifPresent((teleporter -> {
                    if (!teleporter.dimensionId().equals("")) {
                        if (!player.serverLevel().dimension().location().equals(teleporter.getDimIdAsResourceLocation())) {
                            ServerLevel dimension = DimensionUtils.createWorld(player.serverLevel(), teleporter.dimensionId());
                            dimension.getCapability(HammerspaceProvider.HAMMERSPACE).ifPresent((hammerspace -> {
                                hammerspace.setTick(teleporter.tickSpeed());
                                hammerspace.setFluidFlowSpeed(teleporter.fluidFlowSpeed());
                                hammerspace.setGravity(teleporter.gravity());
                            }));
                            teleporter.setFromDimensionTypeId(player.serverLevel().dimensionTypeId().location().toString());
                            teleporter.setLastUsedLocation(new BlockPos((int) player.position().x, (int) player.position().y, (int) player.position().z));
                            player.changeDimension(dimension, new HammerspaceTeleporter(player.getItemInHand(InteractionHand.MAIN_HAND), player.serverLevel(), null));
                        } else {
                            AtomicReference<ServerLevel> toDimension = new AtomicReference<>(Objects.requireNonNull(player.server).overworld());
                            player.server.getAllLevels().forEach((dim -> {
                                if (dim.dimensionTypeId().location().toString().equals(teleporter.fromDimensionTypeId())) {
                                    toDimension.set(dim);
                                }
                            }));
                            if (toDimension.get() != null) {
                                player.changeDimension(toDimension.get(), new HammerspaceTeleporter(player.getItemInHand(InteractionHand.MAIN_HAND), player.serverLevel(), teleporter.lastUsedLocation()));
                            }
                        }
                    }
                }));
            }
        });
        context.get().setPacketHandled(true);
    }
}
