package com.huskydreaming.settlements.services.providers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.reflect.TypeToken;
import com.huskydreaming.settlements.SettlementPlugin;
import com.huskydreaming.settlements.services.interfaces.ClaimService;
import com.huskydreaming.settlements.services.interfaces.ConfigService;
import com.huskydreaming.settlements.storage.types.Json;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClaimServiceImpl implements ClaimService {

    private final ConfigService configService;
    private Map<String, String> claims = new ConcurrentHashMap<>();
    private List<String> disabledWorlds;

    public ClaimServiceImpl(SettlementPlugin settlementPlugin) {
        configService = settlementPlugin.provide(ConfigService.class);
    }

    @Override
    public void setClaim(Chunk chunk, String name) {
        claims.put(parse(chunk), name);
    }

    @Override
    public void removeClaim(Chunk chunk) {
        claims.remove(parse(chunk));
    }

    @Override
    public void clean(String name) {
        getChunksAsStrings(name).forEach(s -> claims.remove(s));
    }

    @Override
    public boolean isClaim(Chunk chunk) {
        return claims.containsKey(parse(chunk));
    }

    @Override
    public String getClaim(Chunk chunk) {
        return claims.get(parse(chunk));
    }

    @Override
    public int getCount() {
        return claims.size();
    }

    @Override
    public LinkedHashMap<String, Long> getTop(int limit) {
        return claims.values().stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public Collection<String> getChunksAsStrings(String name) {
        Multimap<String, String> multiMap = HashMultimap.create();
        for(Map.Entry<String, String> entry : claims.entrySet()) {
            multiMap.put(entry.getValue(), entry.getKey());
        }
        return multiMap.get(name);
    }

    @Override
    public Collection<Chunk> getChunks(String name) {
        Multimap<String, Chunk> multiMap = HashMultimap.create();
        for(Map.Entry<String, String> entry : claims.entrySet()) {
            multiMap.put(entry.getValue(), serialize(entry.getKey()));
        }
        return multiMap.get(name);
    }

    @Override
    public boolean isDisabledWorld(World world) {
        return disabledWorlds.contains(world.getName());
    }


    @Override
    public void serialize(SettlementPlugin plugin) {
        Json.write(plugin, "data/claims", claims);
        plugin.getLogger().info("Saved " + claims.size() + " claim(s).");
    }

    @Override
    public void deserialize(SettlementPlugin plugin) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        claims = Json.read(plugin, "data/claims", type);
        if(claims == null) claims = new ConcurrentHashMap<>();

        int size = claims.size();
        if(size > 0) {
            plugin.getLogger().info("Registered " + size + " claim(s).");
        }

        if(disabledWorlds != null) disabledWorlds.clear();
        disabledWorlds = configService.deserializeDisabledWorlds(plugin);
    }

    private String parse(Chunk chunk) {
        return chunk.getX() + ":" + chunk.getZ() + ":" + chunk.getWorld().getName();
    }

    private Chunk serialize(String string) {
        String[] strings = string.split(":");
        World world = Bukkit.getWorld(strings[2]);
        if (world == null) return null;
        return world.getChunkAt(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
    }
}