
package com.itzpearl.inventoryautototem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;

public class InventoryAutoTotemClient implements ClientModInitializer {

    private boolean openedByMod = false;
    private int cooldown = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Player player = client.player;

            if (player == null || client.level == null) {
                openedByMod = false;
                return;
            }

            if (cooldown > 0) {
                cooldown--;
            }

            boolean offhandEmpty =
                !player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);

            boolean slot8Empty =
                !player.getInventory().getItem(7).is(Items.TOTEM_OF_UNDYING);

            if (!openedByMod) {
                if (cooldown == 0
                    && client.screen == null
                    && (offhandEmpty || slot8Empty)) {

                    client.setScreen(new InventoryScreen(player));
                    openedByMod = true;
                }
                return;
            }

            if (client.screen instanceof InventoryScreen) {
                refill(player, client);
                client.setScreen(null);
                openedByMod = false;
                cooldown = 20;
            } else {
                openedByMod = false;
            }
        });
    }

    private void refill(Player player, Minecraft client) {
        boolean offhandEmpty =
            !player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);

        boolean slot8Empty =
            !player.getInventory().getItem(7).is(Items.TOTEM_OF_UNDYING);

        if (offhandEmpty) {
            moveTotem(player, client, 40);
        }

        if (slot8Empty) {
            moveTotem(player, client, 7);
        }
    }

    private void moveTotem(Player player, Minecraft client, int button) {
        for (int i = 9; i <= 44; i++) {
            Slot slot = player.inventoryMenu.getSlot(i);

            if (i == 43 || !slot.hasItem()) {
                continue;
            }

            if (slot.getItem().is(Items.TOTEM_OF_UNDYING)) {
                client.gameMode.handleInventoryMouseClick(
                    player.inventoryMenu.containerId,
                    i,
                    button,
                    ClickType.SWAP,
                    player
                );
                return;
            }
        }
    }
}
