package com.huskydreaming.settlements.services.interfaces;

import com.huskydreaming.settlements.services.base.ServiceInterface;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface BorderService extends ServiceInterface {

    void addPlayer(Player player, String settlementName);

    void removePlayer(Player player);

    void run(Plugin plugin);
}
