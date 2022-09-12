package net.momirealms.customnameplates.nameplates.bubbles;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.momirealms.customnameplates.ConfigManager;
import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.Function;
import net.momirealms.customnameplates.data.PlayerData;
import net.momirealms.customnameplates.nameplates.ArmorStandManager;
import net.momirealms.customnameplates.nameplates.FakeArmorStand;
import net.momirealms.customnameplates.nameplates.NameplateInstance;
import net.momirealms.customnameplates.nameplates.NameplateUtil;
import net.momirealms.customnameplates.nameplates.mode.EntityTag;
import net.momirealms.customnameplates.resource.ResourceManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ChatBubblesManager extends Function {

    protected final HashMap<Player, ArmorStandManager> armorStandManagerMap = new HashMap<>();

    private BBPacketsHandle packetsHandle;

    public ChatBubblesManager(String name) {
        super(name);
    }

    @Override
    public void load() {

        this.packetsHandle = new BBPacketsHandle("BUBBLES", this);
        this.packetsHandle.load();

        for (Player all : Bukkit.getOnlinePlayers()) {
            armorStandManagerMap.put(all, new ArmorStandManager(all, true));
            for (Player player : Bukkit.getOnlinePlayers())
                spawnArmorStands(player, all);
        }
    }

    @Override
    public void unload() {
        this.packetsHandle.unload();
    }

    private void spawnArmorStands(Player viewer, Player target) {
        if (target == viewer) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (getDistance(target, viewer) < 48 && viewer.canSee(target))
            getArmorStandManager(target).spawn(viewer);
    }

    private double getDistance(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        return Math.sqrt(Math.pow(loc1.getX()-loc2.getX(), 2) + Math.pow(loc1.getZ()-loc2.getZ(), 2));
    }

    public ArmorStandManager getArmorStandManager(Player player) {
        return armorStandManagerMap.get(player);
    }

    public void onJoin(Player player) {
        armorStandManagerMap.put(player, new ArmorStandManager(player, true));
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            spawnArmorStands(viewer, player);
            spawnArmorStands(player, viewer);
        }
    }

    public void onQuit(Player player) {
        ArmorStandManager asm = armorStandManagerMap.remove(player);
        if (asm != null) {
            asm.destroy();
        }
    }
}
