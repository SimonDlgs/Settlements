package com.huskydreaming.settlements.storage.transience;

import org.bukkit.Color;
import org.bukkit.Location;

import java.util.Set;

public record BorderData(Color color, Set<Location> locations) {

}
