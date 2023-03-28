package net.momirealms.customnameplates.api;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.object.nameplate.NameplatesTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomNameplatesAPI {

    private static CustomNameplatesAPI api;
    private final CustomNameplates plugin;

    public CustomNameplatesAPI(CustomNameplates plugin) {
        this.plugin = plugin;
    }

    public void init() {
        api = this;
    }

    public static CustomNameplatesAPI getAPI() {
        return api;
    }

    public boolean doesNameplateExist(String nameplate) {
        return plugin.getNameplateManager().existNameplate(nameplate);
    }

    public boolean doesBubbleExist(String bubble) {
        return plugin.getChatBubblesManager().existBubble(bubble);
    }

    public void equipNameplate(Player player, String nameplate) {
        plugin.getDataManager().equipNameplate(player, nameplate);
        updateAndSave(player);
    }

    private void updateAndSave(Player player) {
        NameplatesTeam nameplatesTeam = plugin.getTeamManager().getNameplateTeam(player.getUniqueId());
        if (nameplatesTeam != null) {
            nameplatesTeam.update(true);
            plugin.getTeamManager().sendUpdateToAll(player, true);
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataManager().saveData(player);
        });
    }

    public void equipBubble(Player player, String bubble) {
        plugin.getDataManager().equipBubble(player, bubble);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataManager().saveData(player);
        });
    }

    public void unEquipNameplate(Player player) {
        plugin.getDataManager().equipNameplate(player, "none");
        updateAndSave(player);
    }

    public void unEquipBubble(Player player) {
        plugin.getDataManager().equipBubble(player, "none");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getDataManager().saveData(player);
        });
    }
}
