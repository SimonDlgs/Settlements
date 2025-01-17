package com.huskydreaming.settlements.services.implementations;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.settlements.dependencies.SettlementPlaceholderExpansion;
import com.huskydreaming.settlements.enumeration.types.DependencyType;
import com.huskydreaming.settlements.services.interfaces.DependencyService;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;

public class DependencyServiceImpl implements DependencyService {

    private final Set<String> types = new HashSet<>();

    @Override
    public boolean isTowny(Player player) {
        if (types.contains(DependencyType.TOWNY.toString())) {
            if(!TownyAPI.getInstance().isTownyWorld(player.getWorld())) return false;
            if(TownyAPI.getInstance().isWilderness(player.getLocation())) return false;

            Town town = TownyAPI.getInstance().getTown(player.getLocation());
            return town != null && town.hasResident(player);
        }
        return false;
    }

    @Override
    public boolean isTowny(Player player, Block block) {
        if (types.contains(DependencyType.TOWNY.toString())) {
            if(!TownyAPI.getInstance().isTownyWorld(block.getWorld())) return false;
            if(TownyAPI.getInstance().isWilderness(block.getLocation())) return false;

            Town town = TownyAPI.getInstance().getTown(block.getLocation());
            return town != null && town.hasResident(player);
        }
        return false;
    }

    @Override
    public boolean isWorldGuard(Player player) {
        if (types.contains(DependencyType.WORLD_GUARD.toString())) {
            Location location = BukkitAdapter.adapt(player.getLocation());
            RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = regionContainer.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(location);
            return set.size() != 0;
        }
        return false;
    }

    @Override
    public boolean isWorldGuard(Block block) {
        if (types.contains(DependencyType.WORLD_GUARD.toString())) {
            Location location = BukkitAdapter.adapt(block.getLocation());
            RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = regionContainer.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(location);
            return set.size() != 0;
        }
        return false;
    }

    @Override
    public void deserialize(HuskyPlugin plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        for (String softDependency : plugin.getDescription().getSoftDepend()) {
            if (pluginManager.getPlugin(softDependency) != null) types.add(softDependency);
        }

        if (!types.isEmpty()) {
            plugin.getLogger().info("Dependencies Found: ");
            types.forEach(type -> plugin.getLogger().info("- " + type));
        } else {
            plugin.getLogger().info("No dependencies found. Using basic version of the plugin.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled(DependencyType.PLACEHOLDER_API.toString())) {
            new SettlementPlaceholderExpansion(plugin).register();
        }
    }
}