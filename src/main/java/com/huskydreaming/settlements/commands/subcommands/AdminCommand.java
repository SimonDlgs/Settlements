package com.huskydreaming.settlements.commands.subcommands;

import com.huskydreaming.huskycore.HuskyPlugin;
import com.huskydreaming.huskycore.commands.Command;
import com.huskydreaming.huskycore.commands.SubCommand;
import com.huskydreaming.huskycore.data.ChunkData;
import com.huskydreaming.huskycore.utilities.Util;
import com.huskydreaming.settlements.commands.CommandLabel;
import com.huskydreaming.settlements.services.interfaces.*;
import com.huskydreaming.settlements.storage.types.Locale;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;


@Command(label = CommandLabel.ADMIN, arguments = " [claim|disband|help|unclaim]")
public class AdminCommand implements SubCommand {

    private final HuskyPlugin plugin;
    private final ClaimService claimService;
    private final FlagService flagService;
    private final InventoryService inventoryService;
    private final MemberService memberService;
    private final RoleService roleService;
    private final SettlementService settlementService;
    private final TrustService trustService;

    public AdminCommand(HuskyPlugin plugin) {
        this.plugin = plugin;

        claimService = plugin.provide(ClaimService.class);
        inventoryService = plugin.provide(InventoryService.class);
        flagService = plugin.provide(FlagService.class);
        roleService = plugin.provide(RoleService.class);
        memberService = plugin.provide(MemberService.class);
        settlementService = plugin.provide(SettlementService.class);
        trustService = plugin.provide(TrustService.class);
    }

    @Override
    public void run(Player player, String[] strings) {
        if (strings.length == 1) {
            inventoryService.getAdminInventory(plugin).open(player);
        } else if (strings.length == 2) {
            String string = strings[1].toUpperCase();

            if (string.equalsIgnoreCase(CommandLabel.UN_CLAIM)) {
                sendUnClaim(player);
            } else if (string.equalsIgnoreCase(CommandLabel.HELP)) {
                sendHelp(player);
            } else {
                sendUnknown(player, string);
            }
        } else if (strings.length == 3) {
            String string = strings[1].toUpperCase();
            if (string.equalsIgnoreCase(CommandLabel.CLAIM)) {
                sendClaim(player, strings[2]);
            } else if (string.equalsIgnoreCase(CommandLabel.DISBAND)) {
                sendDisband(player, strings[2]);
            } else {
                sendUnknown(player, string);
            }
        }
    }

    private void sendClaim(Player player, String string) {
        Chunk chunk = player.getLocation().getChunk();
        if (settlementService.isSettlement(string)) {

            boolean isAdjacent = false;
            for (ChunkData data : claimService.getClaims(string)) {
                if (Util.areAdjacentChunks(chunk, data.toChunk())) isAdjacent = true;
            }

            if (isAdjacent) {
                String x = String.valueOf(chunk.getX());
                String z = String.valueOf(chunk.getZ());

                player.sendMessage(Locale.SETTLEMENT_LAND_CLAIM.prefix(x, z));
                claimService.setClaim(chunk, string);
            } else {
                player.sendMessage(Locale.SETTLEMENT_LAND_ADJACENT.prefix());
            }

        } else {
            player.sendMessage(Locale.SETTLEMENT_NULL.prefix(string));
        }
    }

    private void sendUnClaim(Player player) {
        Chunk chunk = player.getLocation().getChunk();

        if (!claimService.isClaim(chunk)) {
            player.sendMessage(Locale.SETTLEMENT_LAND_NOT_CLAIMED.prefix());
            return;
        }

        String claim = claimService.getClaim(chunk);
        if (claimService.getClaims(claim).size() == 1) {
            player.sendMessage(Locale.SETTLEMENT_LAND_UNCLAIM_ONE.prefix());
        } else {
            claimService.removeClaim(chunk);
            player.sendMessage(Locale.SETTLEMENT_LAND_UNCLAIM.prefix());
        }
    }

    private void sendDisband(Player player, String string) {
        if (!settlementService.isSettlement(string)) {
            player.sendMessage(Locale.SETTLEMENT_NULL.prefix(string));
            return;
        }

        claimService.clean(string);
        memberService.clean(string);
        flagService.clean(string);
        roleService.clean(string);
        trustService.clean(string);
        settlementService.disbandSettlement(string);

        player.sendMessage(Locale.SETTLEMENT_DISBAND_YES.prefix());
    }

    private void sendUnknown(Player player, String string) {
        player.sendMessage(Locale.UNKNOWN_SUBCOMMAND.prefix(string));
    }

    private void sendHelp(Player player) {
        List<String> strings = Locale.ADMIN_HELP.parseList();
        if (strings == null) return;

        strings.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).forEach(player::sendMessage);
    }

    @Override
    public List<String> onTabComplete(Player player, String[] strings) {
        if (strings.length == 2)
            return List.of(CommandLabel.CLAIM, CommandLabel.DISBAND, CommandLabel.HELP, CommandLabel.UN_CLAIM);
        return List.of();
    }
}