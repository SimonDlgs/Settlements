package com.huskydreaming.settlements.storage.persistence;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Settlement {

    private UUID owner;
    private String tag;
    private String description;
    private String defaultRole;
    private Location location;
    private Material icon;
    private int maxLand;
    private int maxCitizens;
    private int maxRoles;

    public static Settlement create(Player player) {
        return new Settlement(player);
    }

    public Settlement(Player player) {
        this.owner = player.getUniqueId();
        this.maxCitizens = 10;
        this.maxLand = 15;
        this.maxRoles = 5;
        this.description = "A peaceful place.";
        this.location = player.getLocation();
    }

    public void setOwner(OfflinePlayer offlinePlayer) {
        this.owner = offlinePlayer.getUniqueId();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setMaxCitizens(int maxCitizens) {
        this.maxCitizens = maxCitizens;
    }

    public void setMaxLand(int maxLand) {
        this.maxLand = maxLand;
    }

    public void setMaxRoles(int maxRoles) {
        this.maxRoles = maxRoles;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public boolean isOwner(OfflinePlayer offlinePlayer) {
        return owner.equals(offlinePlayer.getUniqueId());
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerName() {
        OfflinePlayer player = Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(offlinePlayer -> offlinePlayer.getUniqueId().equals(owner))
                .findFirst().orElse(null);
        if (player != null) return player.getName();
        return null;
    }

    public int getMaxLand() {
        return maxLand;
    }

    public int getMaxCitizens() {
        return maxCitizens;
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public int getMaxRoles() {
        return maxRoles;
    }

    public Material getIcon() {
        return icon;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Settlement that)) return false;
        return maxLand == that.maxLand &&
                maxCitizens == that.maxCitizens &&
                maxRoles == that.maxRoles &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(tag, that.tag) &&
                Objects.equals(description, that.description) &&
                Objects.equals(defaultRole, that.defaultRole) &&
                Objects.equals(location, that.location) &&
                icon == that.icon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, tag, description, defaultRole, location, icon, maxLand, maxCitizens, maxRoles);
    }
}