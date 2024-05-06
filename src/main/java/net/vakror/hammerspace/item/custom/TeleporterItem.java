package net.vakror.hammerspace.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.vakror.hammerspace.capability.TeleporterProvider;
import net.vakror.hammerspace.client.handlers.ClientTeleporterHandler;
import net.vakror.hammerspace.item.ITeleporterTier;
import net.vakror.hammerspace.server.handlers.ServerTeleporterHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeleporterItem extends Item {
    private final ITeleporterTier tier;
    public TeleporterItem(Properties properties, ITeleporterTier tier) {
        super(properties);
        this.tier = tier;
    }
public ITeleporterTier tier() {return tier;}
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!level.isClientSide) {
            ServerTeleporterHandler handler = new ServerTeleporterHandler();
            handler.handleTeleport(player,this, hand);
        } else {
            ClientTeleporterHandler handler = new ClientTeleporterHandler();
            handler.handleTeleport(player, this, hand);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltip, tooltipFlag);
        if (!Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_1", this.tier.getId()));
            tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_newline"));
            tooltip.add(Component.literal("Press §eSHIFT§r for more Information"));
        } else {
            stack.getCapability(TeleporterProvider.TELEPORTER).ifPresent((teleporter -> {
                tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_1", this.tier.getId()));
                tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_newline"));
                if (teleporter.dimensionId().equals("")) {
                    tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_2_not_set"));
                } else {
                    tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_2", teleporter.dimensionId()));
                }
                tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_newline"));


                tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_gravity", teleporter.gravity()));

                if (teleporter.width() == 0) {
                    tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_3_not_set"));
                } else {
                    tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_3", teleporter.width()));
                }

                if (teleporter.height() == 0) {
                    tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_4_not_set"));
                } else {
                    tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_4", teleporter.height()));
                }


                if (teleporter.length() == 0) {
                    tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_5_not_set"));
                } else {
                    tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_5", teleporter.length()));
                }


                tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_newline"));
                tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_7", tier.getMaxWidth()));
                tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_8", tier.getMaxHeight()));
                tooltip.add(Component.translatable("hammerspace.tooltip.teleporter_9", tier.getMaxLength()));
            }));
        }
    }
}
