package net.vakror.hammerspace.handlers;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.vakror.hammerspace.item.custom.TeleporterItem;

public interface ITeleporterHandler {
    void handleTeleport(Player player, TeleporterItem item, InteractionHand hand);
}
