package com.huskydreaming.settlements.inventories.modules.general;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.interfaces.Permission;
import com.huskydreaming.huskycore.inventories.InventoryModule;
import com.huskydreaming.huskycore.utilities.ItemBuilder;
import com.huskydreaming.settlements.enumeration.RolePermission;
import com.huskydreaming.settlements.services.interfaces.InventoryService;
import com.huskydreaming.settlements.storage.types.Menu;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class RoleModule implements InventoryModule {

    private final HuskyPlugin plugin;
    private final InventoryService inventoryService;

    public RoleModule(HuskyPlugin plugin) {
        this.plugin = plugin;
        this.inventoryService = plugin.provide(InventoryService.class);
    }

    @Override
    public ItemStack itemStack(Player player) {
        return ItemBuilder.create()
                .setDisplayName(Menu.SETTLEMENT_ROLES_TITLE.parse())
                .setLore(Menu.SETTLEMENT_ROLES_LORE.parseList())
                .setMaterial(Material.ANVIL)
                .build();
    }

    @Override
    public void run(InventoryClickEvent event, InventoryContents contents) {
        if (event.getWhoClicked() instanceof Player player) {
            inventoryService.getRolesInventory(plugin, player).open(player);
        }
    }

    @Override
    public Permission getPermission() {
        return RolePermission.EDIT_ROLES;
    }
}