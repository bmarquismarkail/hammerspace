package net.vakror.hammerspace.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.vakror.hammerspace.capability.Teleporter;
import net.vakror.hammerspace.capability.TeleporterProvider;
import net.vakror.hammerspace.handlers.ITeleporterHandler;
import net.vakror.hammerspace.item.custom.TeleporterItem;
import net.vakror.hammerspace.screen.TeleporterScreen;

public class ClientTeleporterHandler implements ITeleporterHandler {
    @Override
    public void handleTeleport(Player player, TeleporterItem item, InteractionHand hand) {
        if (!player.isCrouching()) {
            player.getItemInHand(hand).getCapability(TeleporterProvider.TELEPORTER).ifPresent((teleporter -> {
                if (teleporter.dimensionId().equals("")) {
                    Minecraft.getInstance().setScreen(new TeleporterScreen(player.getItemInHand(hand).getCapability(TeleporterProvider.TELEPORTER).orElse(new Teleporter()), hand, item.tier().getMaxWidth(), item.tier().getMaxHeight(), item.tier().getMaxLength(), true, false));
                }
            }));
        } else {
            Minecraft.getInstance().setScreen(new TeleporterScreen(player.getItemInHand(InteractionHand.MAIN_HAND).getCapability(TeleporterProvider.TELEPORTER).orElse(new Teleporter()), hand, item.tier().getMaxWidth(), item.tier().getMaxHeight(), item.tier().getMaxLength(), false, true));
        }

    }
}
