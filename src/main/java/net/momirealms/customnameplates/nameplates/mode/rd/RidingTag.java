package net.momirealms.customnameplates.nameplates.mode.rd;

import net.momirealms.customnameplates.CustomNameplates;
import net.momirealms.customnameplates.nameplates.mode.EntityTag;
import net.momirealms.customnameplates.nameplates.mode.ArmorStandManager;
import net.momirealms.customnameplates.nameplates.mode.EventListenerE;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class RidingTag extends EntityTag {

    private RdPacketsHandler handler;

    private EventListenerE listener;

    public RidingTag(String name) {
        super(name);
    }

    @Override
    public void load() {
        for (Player all : Bukkit.getOnlinePlayers()) {
            armorStandManagerMap.put(all, new ArmorStandManager(all));
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToOne(all);
            CustomNameplates.instance.getTeamPacketManager().sendUpdateToAll(all);
            for (Player player : Bukkit.getOnlinePlayers())
                ridingArmorStands(player, all);
        }
        this.handler = new RdPacketsHandler("RdHandler", this);
        this.handler.load();
        listener = new EventListenerE(this);
        Bukkit.getPluginManager().registerEvents(listener, CustomNameplates.instance);
        super.load();
    }

    @Override
    public void unload() {
        this.handler.unload();
        HandlerList.unregisterAll(listener);
        super.unload();
    }

    @Override
    public void onJoin(Player player) {
        super.onJoin(player);
    }

    @Override
    public void onQuit(Player player) {
        super.onQuit(player);
    }

//    @Override
//    public void onRP(Player player, PlayerResourcePackStatusEvent.Status status) {
//        super.onRP(player, status);
//    }

    @Override
    public ArmorStandManager getArmorStandManager(Player player) {
        return super.getArmorStandManager(player);
    }

    @Override
    public void onSneak(Player player, boolean isSneaking) {
        getArmorStandManager(player).setSneak(isSneaking, false);
    }

    @Override
    public void onRespawn(Player player) {
        //getArmorStandManager(player).teleport();
    }

    private void ridingArmorStands(Player viewer, Player target) {
        if (target == viewer) return;
        if (viewer.getWorld() != target.getWorld()) return;
        if (viewer.canSee(target)) {
            ArmorStandManager asm = getArmorStandManager(target);
            asm.spawn(viewer);
            Bukkit.getScheduler().runTaskLater(CustomNameplates.instance, () -> {
                asm.mount(viewer);
            }, 1);
        }
    }
}
