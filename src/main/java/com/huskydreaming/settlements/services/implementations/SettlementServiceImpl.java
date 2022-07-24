package com.huskydreaming.settlements.services.implementations;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Singleton;
import com.huskydreaming.settlements.SettlementPlugin;
import com.huskydreaming.settlements.persistence.Settlement;
import com.huskydreaming.settlements.services.SettlementService;
import com.huskydreaming.settlements.storage.Json;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class SettlementServiceImpl implements SettlementService {

    private Set<Settlement> settlements = new HashSet<>();

    @Override
    public void createSettlement(String name) {

    }

    @Override
    public void disbandSettlement(Settlement settlement) {
        settlements.removeIf(s -> s.getName().equalsIgnoreCase(settlement.getName()));
    }

    @Override
    public boolean isSettlement(String name) {
        return settlements.stream().anyMatch(settlement -> settlement.getName().equalsIgnoreCase(name));
    }

    @Override
    public Settlement getSettlement(String string) {
        return settlements.stream().filter(settlement -> settlement.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    @Override
    public Set<Settlement> getSettlements() {
        return Collections.unmodifiableSet(settlements);
    }

    @Override
    public void serialize(SettlementPlugin plugin) {
        Json.write(plugin, "settlements", settlements);
    }

    @Override
    public void deserialize(SettlementPlugin plugin) {
        Type type = new TypeToken<Set<Settlement>>(){}.getType();
        Set<Settlement> settlements = Json.read(plugin, "settlements", type);
        if(settlements == null) settlements = new HashSet<>();

        this.settlements = settlements;
    }
}
